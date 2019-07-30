package br.com.sicredi.produto.domain;

import br.com.sicredi.moeda.Price;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {


    public Integer getId() {
        return id;
    }

    private Integer id;
    private String name;
    private Price price;

    public SituacaoProduto getSituacaoProduto() {
        return situacaoProduto;
    }

    private SituacaoProduto situacaoProduto;

    protected Product(final Integer id, final String name, final Price price) {
        this.id = id;
        this.name = name;
        this.price = price;
        semEstoque();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price.getAmount();
    }

    public String getFormattedPrice() {
        return price.toString();
    }

    @JsonIgnore
    public boolean isAtivo() {
        return SituacaoProduto.ATIVO.equals(situacaoProduto);
    }

    public void ativarProduto() {
        this.situacaoProduto = SituacaoProduto.ATIVO;
    }

    public void inativarProduto() {
        this.situacaoProduto = SituacaoProduto.INATIVO;
    }

    public void semEstoque() {
        this.situacaoProduto = SituacaoProduto.SEM_ESTOQUE;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }

    public enum SituacaoProduto {

        ATIVO,
        INATIVO,
        SEM_ESTOQUE;
    }
}
