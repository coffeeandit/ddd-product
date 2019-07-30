package br.com.sicredi.warehouse.infra;

import br.com.sicredi.produto.domain.ProductEntity;
import br.com.sicredi.warehouse.domain.WarehouseMoveEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface WarehouseRepository extends CrudRepository<WarehouseMoveEntity, Integer> {

    Optional<WarehouseMoveEntity> findByProductEntity(final ProductEntity productEntity);
}
