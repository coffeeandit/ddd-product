package br.com.sicredi.warehouse.business;

import br.com.sicredi.config.DomainBusinessException;
import br.com.sicredi.config.InfraestructureException;
import br.com.sicredi.produto.business.ProductBusiness;
import br.com.sicredi.produto.domain.Product;
import br.com.sicredi.warehouse.infra.WarehouseOutSaleProduct;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class WarehouseInOutConsumer {

    private KafkaTemplate<String, String> kafkaTemplate;
    private Logger LOG = LoggerFactory.getLogger(WarehouseInOutConsumer.class);

    private WarehouseBusiness warehouseBusiness;

    private ProductBusiness productBusiness;

    @Value("${app.topic}")
    private String topic;

    private ObjectMapper objectMapper = new ObjectMapper();

    public WarehouseInOutConsumer(final KafkaTemplate<String, String> kafkaTemplate,
                                  final WarehouseBusiness warehouseBusiness,
                                  final ProductBusiness productBusiness
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.productBusiness = productBusiness;
        this.warehouseBusiness = warehouseBusiness;
    }

    @KafkaListener(topics = "${app.topic}")
    @Retryable(backoff = @Backoff(delay = 3 * 1000))
    public void onConsume(final String message) throws IOException {


        var warehouseInOutProduct = objectMapper.readValue(message, WarehouseOutSaleProduct.class);
        LOG.info("Recebendo mensagem de produto.: " + message);
        try {
            Optional<Product> product = productBusiness.findById(warehouseInOutProduct.getIdProduct());

            if (product.isPresent()) {
                final var productEvent = product.get();
                if (productEvent.isAtivo()) {
                    if (Integer.valueOf(0).equals(warehouseInOutProduct.getQuantity())) {
                        throw new InfraestructureException("Vai que aumente o número em 3 tentativas!!!");
                    }
                    warehouseBusiness.addProduct(productEvent, warehouseInOutProduct.getQuantity());
                } else {
                    throw new DomainBusinessException(String.format("Produto %s está inativo ", productEvent.getName()));
                }
            } else {
                throw new DomainBusinessException("Produto " + warehouseInOutProduct.getIdProduct() + " não existe.");
            }


        } catch (Exception runtimeException) {
            throw new RuntimeException(runtimeException);
        }

    }
}
