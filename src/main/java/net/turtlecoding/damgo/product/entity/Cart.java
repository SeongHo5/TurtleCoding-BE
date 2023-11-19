package net.turtlecoding.damgo.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.turtlecoding.damgo.account.entity.Account;
import net.turtlecoding.damgo.common.entity.BaseEntity;

@Entity
@Getter
@Table(name = "carts")
@NoArgsConstructor
public class Cart {

    @EmbeddedId
    private CartId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Account user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Builder
    public Cart(Account account, Product product, Integer quantity) {
        this.id = new CartId(account);
        this.user = account;
        this.product = product;
        this.quantity = quantity;
    }


}
