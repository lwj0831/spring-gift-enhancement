package gift.product.validation;

import gift.product.dto.CreateProductOptionRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UniqueProductOptionNameValidator implements
    ConstraintValidator<UniqueOptionNames, List<CreateProductOptionRequestDto>> {

    @Override
    public boolean isValid(List<CreateProductOptionRequestDto> dto,
        ConstraintValidatorContext context) {
        List<String> names = dto.stream()
            .map(CreateProductOptionRequestDto::name)
            .toList();

        Set<String> uniqueNames = new HashSet<>(names);
        return uniqueNames.size() == names.size();
    }
}
