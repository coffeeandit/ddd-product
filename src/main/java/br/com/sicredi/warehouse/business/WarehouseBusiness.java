package br.com.sicredi.warehouse.business;

import br.com.sicredi.SpringApplication;
import br.com.sicredi.produto.business.ProductBusiness;
import br.com.sicredi.produto.domain.Product;
import br.com.sicredi.produto.domain.ProductBuilder;
import br.com.sicredi.produto.domain.ProductEntity;
import br.com.sicredi.warehouse.domain.WarehouseMove;
import br.com.sicredi.warehouse.domain.WarehouseMoveEntity;
import br.com.sicredi.warehouse.infra.WarehouseObserver;
import br.com.sicredi.warehouse.infra.WarehouseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class WarehouseBusiness {

    private Set<WarehouseObserver> warehouseObserverSet;


    private Logger LOG = LoggerFactory.getLogger(SpringApplication.class);
    private WarehouseRepository warehouseRepository;
    private ProductBusiness productBusiness;

    public WarehouseBusiness(WarehouseRepository warehouseRepository, ProductBusiness productBusiness) {
        this.warehouseRepository = warehouseRepository;
        this.productBusiness = productBusiness;
        this.warehouseObserverSet = new HashSet<>();
        this.warehouseObserverSet.add((product, quantity) -> {

            storeProduct(product, quantity);
        });
    }

    private void storeProduct(Product product, Integer quantity) {
        var productBuilder = new ProductBuilder();
        ProductEntity productEntity = productBuilder.toDomain(product);
        Optional<WarehouseMoveEntity> warehouseMoveEntity
                = warehouseRepository.findByProductEntity(productEntity);
        int value = atualizarSituacaoProduto(product, quantity, warehouseMoveEntity);

        if (warehouseMoveEntity.isPresent()) {
            WarehouseMoveEntity warehouseMoveEntityUpdate = warehouseMoveEntity.get();
            warehouseMoveEntityUpdate.setQuantity(value);
            warehouseRepository.save(warehouseMoveEntityUpdate);
        } else {
            var warehouseMoveEntityInsert = new WarehouseMoveEntity();
            warehouseMoveEntityInsert.setQuantity(value);
            warehouseMoveEntityInsert.setDateOfMovement(LocalDateTime.now());
            warehouseMoveEntityInsert.setProductEntity(productEntity);
            warehouseRepository.save(warehouseMoveEntityInsert);
        }


    }

    private int atualizarSituacaoProduto(Product product, Integer quantity, Optional<WarehouseMoveEntity> warehouseMoveEntity) {
        var actualValue = warehouseMoveEntity.isPresent() ? warehouseMoveEntity.get().getQuantity() : 0;
        var value = actualValue + quantity;
        if (value > 0) product.ativarProduto();
        else product.semEstoque();

        productBusiness.atualizarSituacaoProduto(product);

        LOG.info(String.format("Total no estoque do produto %s = %d", product.getName(),
                value));
        return value;
    }

    public WarehouseMove addProduct(final Product product, final Integer quantity) {
        return setQuantityProduct(product, quantity);
    }

    public WarehouseMove removeProduct(final Product product, final Integer quantity) {
        return setQuantityProduct(product, -quantity);
    }

    private WarehouseMove setQuantityProduct(final Product product, final Integer quantity) {
        warehouseObserverSet.forEach(warehouseObserver -> {
            warehouseObserver.doMovement(product, quantity);
        });
        return new WarehouseMove(product, quantity);
    }

    public Map<String, Integer> getProductDeposits() {

        var productBuilder = new ProductBuilder();

        var map = new HashMap<String, Integer>();
        Iterable<WarehouseMoveEntity> all = warehouseRepository.findAll();
        all.forEach(warehouseMoveEntity -> {
            map.put(warehouseMoveEntity.getProductEntity().getName(),
                    warehouseMoveEntity.getQuantity());

        });


        return map;
    }
}
