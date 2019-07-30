package br.com.sicredi.produto.infra;

import br.com.sicredi.produto.domain.ProductEntity;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<ProductEntity, Integer> {
}
