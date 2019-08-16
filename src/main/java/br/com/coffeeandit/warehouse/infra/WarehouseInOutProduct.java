package br.com.coffeeandit.warehouse.infra;

import javax.validation.constraints.NotNull;

public class WarehouseInOutProduct {
    public Integer getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(Integer idProduct) {
        this.idProduct = idProduct;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @NotNull
    private Integer idProduct;
    @NotNull
    private Integer quantity;



}
