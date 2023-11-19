package net.turtlecoding.damgo.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.turtlecoding.damgo.account.entity.Account;
import net.turtlecoding.damgo.account.repository.AccountRepository;
import net.turtlecoding.damgo.common.exception.NotFoundException;
import net.turtlecoding.damgo.product.dto.CartAddRequestDto;
import net.turtlecoding.damgo.product.entity.Cart;
import net.turtlecoding.damgo.product.entity.Product;
import net.turtlecoding.damgo.product.repository.CartRepository;
import org.springframework.stereotype.Service;
import net.turtlecoding.damgo.product.repository.ProductRepository;
import org.springframework.transaction.annotation.Transactional;

import static net.turtlecoding.damgo.common.exception.enums.ExceptionStatus.NOT_FOUND_ACCOUNT;
import static net.turtlecoding.damgo.common.exception.enums.ExceptionStatus.NOT_FOUND_PRODUCT;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;
    private final CartRepository cartRepository;


    @Transactional
    public void addProductToCart(final CartAddRequestDto addCartRequestDto) {

        Account account = accountRepository.findByEmail(addCartRequestDto.getUserEmail())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ACCOUNT));
        log.info("account: {}", account.getName());
        Product product = productRepository.findByProdId(addCartRequestDto.getProductId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PRODUCT));
        log.info("product: {}", product.getName());

        Cart cart = addCartRequestDto.toEntity(account, product);
        log.info("cart: {}", cart.getId());

        cartRepository.save(cart);
    }



}
