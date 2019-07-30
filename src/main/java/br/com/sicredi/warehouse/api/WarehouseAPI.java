package br.com.sicredi.warehouse.api;

import br.com.sicredi.produto.business.ProductBusiness;
import br.com.sicredi.produto.domain.Product;
import br.com.sicredi.warehouse.business.WarehouseBusiness;
import br.com.sicredi.warehouse.domain.WarehouseMove;
import br.com.sicredi.warehouse.infra.WarehouseInOutProduct;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@RestController
public class WarehouseAPI {

    private WarehouseBusiness warehouseBusiness;
    private ProductBusiness productBusiness;

    public WarehouseAPI(final WarehouseBusiness warehouseBusiness, final ProductBusiness productBusiness) {
        this.warehouseBusiness = warehouseBusiness;
        this.productBusiness = productBusiness;
    }

    @RequestMapping(value = "/warehouse/inOutProduct", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST

    )
    public ResponseEntity<WarehouseMove> addProduct(@Valid @RequestBody final WarehouseInOutProduct warehouseInOutProduct) {

        Optional<Product> product = productBusiness.findById(warehouseInOutProduct.getIdProduct());
        if (product.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(warehouseBusiness.addProduct(product.get(), warehouseInOutProduct.getQuantity()));
    }

    @RequestMapping(value = "/warehouse", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET

    )
    public ResponseEntity<Map<String, Integer>> getWarehouse(@RequestHeader(value = "Content-type", defaultValue = MediaType.APPLICATION_JSON_VALUE) final String contentType) {

        return ResponseEntity.ok(warehouseBusiness.getProductDeposits());
    }

}
