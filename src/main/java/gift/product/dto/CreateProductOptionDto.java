package gift.product.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProductOptionDto(
    @Size(max = 50, message = "옵션 이름은 최대 50자까지 입력할 수 있습니다.")
    @NotNull
    String name,

    @NotNull
    int quantity
) {

}
