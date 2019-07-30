package br.com.sicredi.warehouse.infra;

import br.com.sicredi.produto.domain.Product;

@FunctionalInterface
public interface WarehouseObserver {

    void doMovement(final Product product, final Integer quantity);
}
