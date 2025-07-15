package gift.product.jpa;

import gift.product.domain.Product;

public class ProductMapper {

  public static Product toDomain(ProductEntity entity) {
    return new Product(entity.getId(), entity.getName(), entity.getPrice(), entity.getDescription(),
        entity.getImageUrl());
  }

  public static ProductEntity toEntity(Product domain) {
    return new ProductEntity(domain.id(), domain.name(), domain.price(), domain.description(),
        domain.imageUrl());
  }
}
