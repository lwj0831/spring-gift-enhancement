package gift.wishlist.dto;

import gift.product.domain.Product;

public record GetWishItemResponseDto(
    Long productId,
    String productName,
    int price,
    String imageUrl

) {

    public static GetWishItemResponseDto from(Product product) {
        return new GetWishItemResponseDto(product.getId(), product.getName(), product.getPrice(),
            product.getImageUrl());
    }

    public static GetWishItemResponseDto from(SimpleWishItemDto dto) {
        return new GetWishItemResponseDto(dto.productId(), dto.name(), dto.price(), dto.imageUrl());
    }

}
