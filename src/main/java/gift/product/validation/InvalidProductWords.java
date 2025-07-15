package gift.product.validation;

import java.util.List;

public class InvalidProductWords {

  private final List<String> invalidWordList;

  private InvalidProductWords(List<String> words) {
    this.invalidWordList = words.stream().toList();
  }

  public static InvalidProductWords getInvalidWords() {
    return new InvalidProductWords(List.of("카카오"));
  }

  public boolean contains(String productName) {
    return invalidWordList.stream()
        .anyMatch(productName::contains);
  }

  public List<String> findMatches(String productName) {
    return invalidWordList.stream()
        .filter(productName::contains)
        .toList();
  }

}
