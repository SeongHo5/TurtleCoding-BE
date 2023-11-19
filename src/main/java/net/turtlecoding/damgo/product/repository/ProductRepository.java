package net.turtlecoding.damgo.product.repository;

import net.turtlecoding.damgo.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p where p.prodId = ?1")
    Optional<Product> findByProdId(String prodId);


}
