package gift.global.common.dto;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record PagedResult<T>(
    List<T> content,
    int page,
    int size,
    boolean hasNext,
    boolean hasPrevious
) {

  public static <T> PagedResult<T> of(List<T> content, int page, int size) {
    boolean hasNext = content.size() > size;
    boolean hasPrevious = page > 0;
    List<T> actualContent = hasNext ? content.subList(0, size) : content;

    return new PagedResult<>(actualContent, page, size, hasNext, hasPrevious);
  }

  public <U> PagedResult<U> map(Function<T, U> mapper) {
    List<U> mappedContent = this.content.stream()
        .map(mapper)
        .collect(Collectors.toList());

    return new PagedResult<>(mappedContent, page, size, hasNext, hasPrevious);
  }
}
