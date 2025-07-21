package gift.product.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateProductOptionRequestDto(
    @Size(max = 50, message = "옵션 이름은 최대 50자까지 입력할 수 있습니다.")
    @Pattern(
        regexp = "^[\\p{L}\\p{N} ()\\[\\]\\+\\-\\&/_]*$",
        message = "허용되지 않은 특수 문자가 포함되어 있습니다."
    )
    @NotNull
    String name,

    @Min(1)
    @Max(99_999_999)
    @NotNull
    int quantity
) {

}
