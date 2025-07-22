package gift.product.dto;

import gift.product.validation.UniqueOptionNames;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@UniqueOptionNames
public record CreateProductOptionListRequestDto(
    @Valid
    @NotEmpty(message = "상품은 하나 이상의 옵션을 가져야 합니다.")
    List<CreateProductOptionRequestDto> optionRequestDtoList
) {

}
