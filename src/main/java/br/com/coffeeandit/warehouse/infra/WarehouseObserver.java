package br.com.coffeeandit.warehouse.infra;

import br.com.coffeeandit.produto.domain.Product;

@FunctionalInterface
public interface WarehouseObserver {

    void doMovement(final Product product, final Integer quantity);
}
