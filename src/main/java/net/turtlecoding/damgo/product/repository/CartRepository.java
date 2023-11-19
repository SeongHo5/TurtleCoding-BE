package net.turtlecoding.damgo.product.repository;

import net.turtlecoding.damgo.product.entity.Cart;
import net.turtlecoding.damgo.product.entity.CartId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, CartId> {
}
