package br.com.coffeeandit.warehouse.infra;

import br.com.coffeeandit.produto.domain.ProductEntity;
import br.com.coffeeandit.warehouse.domain.WarehouseMoveEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface WarehouseRepository extends CrudRepository<WarehouseMoveEntity, Integer> {

    Optional<WarehouseMoveEntity> findByProductEntity(final ProductEntity productEntity);
}
