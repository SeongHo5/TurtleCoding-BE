package net.turtlecoding.damgo.product.dto;

import lombok.Builder;
import lombok.Value;
import net.turtlecoding.damgo.account.dto.SignInResponseDto;
import net.turtlecoding.damgo.account.enums.UserRole;

@Value
public class ProductInfoResponseDto {

    String imageUrl;
    String category;
    String name;
    int price;

    @Builder
    public ProductInfoResponseDto(String imageUrl, String category, String name, int price) {
        this.imageUrl = imageUrl;
        this.category = category;
        this.name = name;
        this.price = price;
    }

    public static ProductInfoResponseDto of(
            String imageUrl, String category, String name, int price
    ) {
        return new ProductInfoResponseDto(imageUrl, category, name, price);
    }

}
