package br.com.coffeeandit.produto.infra;

public class ProductInactivationTransport {


    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    private boolean inactive = true;
}
