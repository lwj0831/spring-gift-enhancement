package gift.product.validation;

import gift.product.exception.InvalidProductNameException;
import gift.product.exception.InvalidProductSortFieldException;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {

  public void validateProductName(String productName) {
    if (InvalidProductWords.contains(productName)) {
      List<String> foundWords = InvalidProductWords.findMatches(productName);
      throw new InvalidProductNameException(
          "상품명에 다음 키워드를 포함할 수 없습니다: " + String.join(", ", foundWords)
      );
    }
  }

  public void validateProductSortField(String sortField) {
    if (!ValidProductSortFields.contains(sortField)) {
      throw new InvalidProductSortFieldException();
    }
  }

}
