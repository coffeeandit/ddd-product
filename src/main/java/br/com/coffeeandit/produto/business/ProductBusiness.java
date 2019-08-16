package br.com.coffeeandit.produto.business;

import br.com.coffeeandit.produto.domain.Product;
import br.com.coffeeandit.produto.domain.ProductBuilder;
import br.com.coffeeandit.produto.domain.ProductEntity;
import br.com.coffeeandit.produto.infra.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class ProductBusiness {

    private ProductRepository productRepository;

    public ProductBusiness(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(final String name, final BigDecimal price) {
        var productBuilder = new ProductBuilder();

        var product = productBuilder.withName(name).withPrice(price)
                .createProduct();
        return productBuilder.toTransport(productRepository.save(productBuilder.toDomain(product)));

    }

    public void atualizarSituacaoProduto(final Product product) {
        var productOptional = productRepository.findById(product.getId());
        if (productOptional.isPresent()) {
            ProductEntity productEntity = productOptional.get();
            productEntity.setSituacaoProduto(product.getSituacaoProduto());
            productRepository.save(productEntity);

        }

    }

    public Optional<Product> inativarProduto(final Integer id) {
        var product = productRepository.findById(id);
        if (product.isPresent()) {
            var productBuilder = new ProductBuilder();
            ProductEntity productEntity = product.get();
            productEntity.setSituacaoProduto(Product.SituacaoProduto.INATIVO);
            return Optional.of(
                    productBuilder.toTransport(productRepository.save(productEntity)));
        }
        return Optional.empty();
    }

    public Optional<Product> findById(Integer id) {

        var productRepositoryById = productRepository.findById(id);

        if (productRepositoryById.isPresent()) {
            var productBuilder = new ProductBuilder();

            return Optional.of(productBuilder.toTransport(productRepositoryById.get()));
        }
        return Optional.empty();
    }
}
