package net.turtlecoding.damgo.product.controller;

import lombok.RequiredArgsConstructor;
import net.turtlecoding.damgo.common.service.FileService;
import net.turtlecoding.damgo.product.dto.CartAddRequestDto;
import net.turtlecoding.damgo.product.dto.ProductInfoResponseDto;
import net.turtlecoding.damgo.product.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URL;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;
    private final FileService fileService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_SELLER') or hasRole('ROLE_ADMIN')")
    public void addProductToCart(
            final @RequestBody CartAddRequestDto addCartRequestDto
    ) {
        productService.addProductToCart(addCartRequestDto);
    }

    @GetMapping("/info/{prodid}")
    public ResponseEntity<ProductInfoResponseDto> getProductInfo(
            final @PathVariable("prodid") String prodId
    ) {
        return fileService.getFileUrl(prodId);
    }

}
