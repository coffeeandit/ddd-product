package br.com.sicredi.warehouse.infra;

public class WarehouseOutSaleProduct extends WarehouseInOutProduct {

    public Integer getIdSale() {
        return idSale;
    }

    public void setIdSale(Integer idSale) {
        this.idSale = idSale;
    }

    private Integer idSale;
}
