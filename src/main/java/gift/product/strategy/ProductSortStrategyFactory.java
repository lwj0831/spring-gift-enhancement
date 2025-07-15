package gift.product.strategy;

import gift.global.common.dto.SortInfo;
import gift.global.common.strategy.SortStrategy;
import gift.product.domain.Product;
import gift.product.exception.InvalidProductSortFieldException;
import java.util.Comparator;
import java.util.Map;

public class ProductSortStrategyFactory {

  private static final Map<String, SortStrategy<Product>> strategyMap = Map.of(
      "id", new ProductIdSortStrategy(),
      "name", new ProductNameSortStrategy(),
      "price", new ProductPriceSortStrategy()
  );

  private static SortStrategy<Product> getStrategy(String sortField)
      throws InvalidProductSortFieldException {
    SortStrategy<Product> strategy = strategyMap.get(sortField);
    if (strategy == null) {
      throw new InvalidProductSortFieldException("정렬 필드 값이 올바르지 않습니다");
    }
    return strategy;
  }

  public static Comparator<Product> getComparator(SortInfo sortInfo) {
    String sortField = sortInfo.field();
    boolean isAscending = sortInfo.isAscending();

    SortStrategy<Product> sortStrategy = ProductSortStrategyFactory.getStrategy(sortField);

    return isAscending ?
        sortStrategy.getComparator() :
        sortStrategy.getComparator().reversed();
  }
}
