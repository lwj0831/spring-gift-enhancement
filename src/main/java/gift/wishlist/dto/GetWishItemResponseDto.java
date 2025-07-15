package gift.wishlist.dto;

import gift.product.domain.Product;
import gift.product.dto.SimpleWishItemDto;

public record GetWishItemResponseDto(
    Long productId,
    String productName,
    int price,
    String imageUrl

) {

  public static GetWishItemResponseDto from(Product product) {
    return new GetWishItemResponseDto(product.id(), product.name(), product.price(),
        product.imageUrl());
  }

  public static GetWishItemResponseDto from(SimpleWishItemDto dto) {
    return new GetWishItemResponseDto(dto.productId(), dto.name(), dto.price(), dto.imageUrl());
  }

}
