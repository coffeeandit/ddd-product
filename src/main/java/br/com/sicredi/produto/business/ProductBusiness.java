package br.com.sicredi.produto.business;

import br.com.sicredi.produto.domain.Product;
import br.com.sicredi.produto.domain.ProductBuilder;
import br.com.sicredi.produto.domain.ProductEntity;
import br.com.sicredi.produto.infra.ProductRepository;
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
        Optional<ProductEntity> productOptional = productRepository.findById(product.getId());
        if (productOptional.isPresent()) {
            ProductEntity productEntity = productOptional.get();
            productEntity.setSituacaoProduto(product.getSituacaoProduto());
            productRepository.save(productEntity);

        }

    }

    public Optional<Product> inativarProduto(final Integer id) {
        Optional<ProductEntity> product = productRepository.findById(id);
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

        Optional<ProductEntity> productRepositoryById = productRepository.findById(id);

        if (productRepositoryById.isPresent()) {
            var productBuilder = new ProductBuilder();

            return Optional.of(productBuilder.toTransport(productRepositoryById.get()));
        }
        return Optional.empty();
    }
}