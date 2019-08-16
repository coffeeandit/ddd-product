package br.com.coffeeandit.warehouse.business;

import br.com.coffeeandit.SpringApplication;
import br.com.coffeeandit.produto.business.ProductBusiness;
import br.com.coffeeandit.produto.domain.Product;
import br.com.coffeeandit.produto.domain.ProductBuilder;
import br.com.coffeeandit.produto.domain.ProductEntity;
import br.com.coffeeandit.warehouse.domain.WarehouseMove;
import br.com.coffeeandit.warehouse.domain.WarehouseMoveEntity;
import br.com.coffeeandit.warehouse.infra.WarehouseObserver;
import br.com.coffeeandit.warehouse.infra.WarehouseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;

@Service
public class WarehouseBusiness {

    private Set<WarehouseObserver> warehouseObserverSet;

    private Logger LOG = LoggerFactory.getLogger(SpringApplication.class);
    private WarehouseRepository warehouseRepository;
    private ProductBusiness productBusiness;

    public WarehouseBusiness(final WarehouseRepository warehouseRepository, final ProductBusiness productBusiness) {
        this.warehouseRepository = warehouseRepository;
        this.productBusiness = productBusiness;
        this.warehouseObserverSet = new HashSet<>();
        this.warehouseObserverSet.add((product, quantity) -> {

            storeProduct(product, quantity, new BiFunction<ProductEntity, Integer, WarehouseMoveEntity>() {
                public WarehouseMoveEntity apply(ProductEntity productEntity, Integer value) {
                    var warehouseMoveEntityInsert = new WarehouseMoveEntity();
                    warehouseMoveEntityInsert.setQuantity(value);
                    warehouseMoveEntityInsert.setDateOfMovement(LocalDateTime.now());
                    warehouseMoveEntityInsert.setProductEntity(productEntity);
                    return warehouseMoveEntityInsert;
                }
            });
        });
    }

    private void storeProduct(final Product product, final Integer quantity, final BiFunction<ProductEntity, Integer, WarehouseMoveEntity> moveEntityBiFunction) {
        var productBuilder = new ProductBuilder();
        var productEntity = productBuilder.toDomain(product);
        Optional<WarehouseMoveEntity> warehouseMoveEntity
                = warehouseRepository.findByProductEntity(productEntity);
        int value = atualizarSituacaoProduto(product, quantity, warehouseMoveEntity);

        if (warehouseMoveEntity.isPresent()) {
            var warehouseMoveEntityUpdate = warehouseMoveEntity.get();
            warehouseMoveEntityUpdate.setQuantity(value);
            warehouseRepository.save(warehouseMoveEntityUpdate);
        } else {
            var warehouseMoveEntityInsert = moveEntityBiFunction.apply(productEntity, value);
            warehouseRepository.save(warehouseMoveEntityInsert);
        }


    }

    private int atualizarSituacaoProduto(Product product, Integer quantity, Optional<WarehouseMoveEntity> warehouseMoveEntity) {
        var actualValue = warehouseMoveEntity.isPresent() ? warehouseMoveEntity.get().getQuantity() : 0;
        int value = analisarEstoque(product, quantity, actualValue);

        productBusiness.atualizarSituacaoProduto(product);

        LOG.info(String.format("Total no estoque do produto %s = %d", product.getName(),
                value));
        return value;
    }

    private int analisarEstoque(Product product, Integer quantity, int actualValue) {
        var value = actualValue + quantity;
        if (value > 0) product.ativarProduto();
        else product.semEstoque();
        return value;
    }

    public WarehouseMove addProduct(final Product product, final Integer quantity) {
        return setQuantityProduct(product, quantity);
    }

    public WarehouseMove substractProduct(final Product product, final Integer quantity) {
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
