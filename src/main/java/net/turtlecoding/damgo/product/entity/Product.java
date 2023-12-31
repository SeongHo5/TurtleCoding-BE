package net.turtlecoding.damgo.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.turtlecoding.damgo.common.entity.BaseEntity;


@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "prodId", nullable = false)
    private String prodId;

    @Size(max = 255)
    @NotNull
    @Column(name = "category", nullable = false)
    private String category;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "price", nullable = false)
    private Integer price;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Builder
    public Product(String prodId, String category, String name, Integer price, Integer quantity) {
        this.prodId = prodId;
        this.category = category;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

}
