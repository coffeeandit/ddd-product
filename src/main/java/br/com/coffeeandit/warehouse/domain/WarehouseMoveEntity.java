package br.com.coffeeandit.warehouse.domain;

import br.com.coffeeandit.produto.domain.ProductEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Table(name = "warehouse_move_entity")
@Entity
public class WarehouseMoveEntity {
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ProductEntity getProductEntity() {
        return productEntity;
    }

    public void setProductEntity(ProductEntity productEntity) {
        this.productEntity = productEntity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getDateOfMovement() {
        return dateOfMovement;
    }

    public void setDateOfMovement(LocalDateTime dateOfMovement) {
        this.dateOfMovement = dateOfMovement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarehouseMoveEntity that = (WarehouseMoveEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Id
    @GeneratedValue
    private Integer id;
    @JoinColumn(name = "id_product", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private ProductEntity productEntity;
    private Integer quantity;
    private LocalDateTime dateOfMovement;

}
