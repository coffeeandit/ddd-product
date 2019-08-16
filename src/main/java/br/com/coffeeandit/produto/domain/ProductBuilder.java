package br.com.coffeeandit.produto.domain;

import br.com.coffeeandit.moeda.Price;

import java.math.BigDecimal;
import java.util.Objects;

public class ProductBuilder {
    private String name;
    private Price price;
    private Integer id;

    public ProductBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public ProductBuilder withPrice(final BigDecimal price) {
        this.price = new Price(price);
        return this;
    }

    public Product createProduct() {
        Objects.requireNonNull(name, "set the name");
        Objects.requireNonNull(price, "set the price");
        return new Product(id, name, price);
    }

    public ProductEntity toDomain(final Product product) {

        var productModel = new ProductEntity();
        productModel.setName(product.getName());
        productModel.setId(product.getId());
        productModel.setSituacaoProduto(product.getSituacaoProduto());
        productModel.setPrice(product.getPrice());
        return productModel;

    }

    public Product toTransport(final ProductEntity product) {

        var productModel = withId(product.getId()).
                withName(product.getName()).withPrice(product.getPrice()).createProduct();
        switch (product.getSituacaoProduto()) {
            case ATIVO:
                productModel.ativarProduto();
                break;
            case INATIVO:
                productModel.inativarProduto();
                break;
            default:
                productModel.semEstoque();
                break;
        }
        return productModel;

    }
}