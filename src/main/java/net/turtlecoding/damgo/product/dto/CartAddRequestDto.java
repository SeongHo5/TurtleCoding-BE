package net.turtlecoding.damgo.product.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;
import net.turtlecoding.damgo.account.entity.Account;
import net.turtlecoding.damgo.product.entity.Cart;
import net.turtlecoding.damgo.product.entity.Product;

/**
 * DTO for {@link Cart}
 */
@Value
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CartAddRequestDto {

    String userEmail;

    String productId;

    int quantity;


    public Cart toEntity(Account account, Product product) {
        return Cart.builder()
                .account(account)
                .product(product)
                .quantity(this.quantity)
                .build();
    }
}
