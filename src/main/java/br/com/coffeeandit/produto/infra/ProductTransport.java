package br.com.coffeeandit.produto.infra;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ProductTransport {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @NotNull
    private String name;
    @NotNull
    private BigDecimal price;
}
