package gift.product.dto;

import gift.product.domain.Product;

public record GetProductResponseDto(
    Long id,
    String name,
    int price,
    String description,
    String imageUrl
) {

  public static GetProductResponseDto from(Product product) {
    return new GetProductResponseDto(product.id(), product.name(), product.price(),
        product.description(), product.imageUrl());
  }
}
