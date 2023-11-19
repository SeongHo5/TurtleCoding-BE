package net.turtlecoding.damgo.product.repository;

import net.turtlecoding.damgo.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
