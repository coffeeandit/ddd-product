package br.com.coffeeandit.warehouse.domain;

import br.com.coffeeandit.produto.domain.Product;

import java.time.LocalDateTime;

public class WarehouseMove {

    public Product getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public WarehouseMove(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
        this.dateOfMovement = LocalDateTime.now();
    }

    private Product product;
    private Integer quantity;

    public LocalDateTime getDateOfMovement() {
        return dateOfMovement;
    }

    private LocalDateTime dateOfMovement;
}
