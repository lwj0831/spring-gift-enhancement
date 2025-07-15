package gift.product.validation;

import gift.product.exception.InvalidProductNameException;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {

  private final InvalidProductWords invalidWords;

  public ProductValidator() {
    this.invalidWords = InvalidProductWords.getInvalidWords();
  }

  public void validateProductName(String productName) {
    if (invalidWords.contains(productName)) {
      List<String> foundWords = invalidWords.findMatches(productName);
      throw new InvalidProductNameException(
          "상품명에 다음 키워드를 포함할 수 없습니다: " + String.join(", ", foundWords)
      );
    }
  }

}
