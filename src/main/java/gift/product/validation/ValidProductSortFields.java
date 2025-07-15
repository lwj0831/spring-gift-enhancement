package gift.product.validation;

import java.util.List;

public class ValidProductSortFields {

  private static final List<String> invalidSortFields = List.of("id", "name", "price");

  public static boolean contains(String keyword) {
    return invalidSortFields.stream()
        .anyMatch(keyword::contains);
  }

}
