package gift.product.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.global.common.dto.PageResponseDto;
import gift.global.exception.GlobalExceptionHandler;
import gift.product.dto.CreateProductOptionRequestDto;
import gift.product.dto.GetProductOptionResponseDto;
import gift.product.service.ProductOptionService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ProductOptionControllerTest {

    @Mock
    private ProductOptionService productOptionService;

    @InjectMocks
    private ProductOptionController productOptionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(productOptionController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    @DisplayName("상품 옵션 등록 API 테스트 - 성공")
    void registerProductOption_Success() throws Exception {
        // given
        Long productId = 1L;
        CreateProductOptionRequestDto dto = new CreateProductOptionRequestDto("테스트 옵션", 100);

        when(productOptionService.registerProductOption(productId, dto)).thenReturn(1L);

        // when & then
        mockMvc.perform(post("/api/products/{productId}/options", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());

        verify(productOptionService).registerProductOption(productId, dto);
    }

    @Test
    @DisplayName("상품 옵션 등록 API 테스트 - 유효성 검증 실패 (옵션명 길이 초과)")
    void registerProductOption_ValidationFail_NameTooLong() throws Exception {
        // given
        Long productId = 1L;
        String longName = "a".repeat(51); // 51자
        CreateProductOptionRequestDto dto = new CreateProductOptionRequestDto(longName, 100);

        // when & then
        mockMvc.perform(post("/api/products/{productId}/options", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(
                jsonPath("$.extras.invalid-params[0].reason").value("옵션 이름은 최대 50자까지 입력할 수 있습니다."));
        ;
    }

    @Test
    @DisplayName("상품 옵션 등록 API 테스트 - 유효성 검증 실패 (허용되지 않는 특수문자)")
    void registerProductOption_ValidationFail_InvalidCharacter() throws Exception {
        // given
        Long productId = 1L;
        CreateProductOptionRequestDto dto = new CreateProductOptionRequestDto("옵션@#$", 100);

        // when & then
        mockMvc.perform(post("/api/products/{productId}/options", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(
                jsonPath("$.extras.invalid-params[0].reason").value("허용되지 않은 특수 문자가 포함되어 있습니다."));
    }

    @Test
    @DisplayName("상품 옵션 등록 API 테스트 - 유효성 검증 실패 (수량 범위 초과)")
    void registerProductOption_ValidationFail_QuantityExceedsMaximum() throws Exception {
        // given
        Long productId = 1L;
        CreateProductOptionRequestDto dto = new CreateProductOptionRequestDto("옵션명", 100_000_000);

        // when & then
        mockMvc.perform(post("/api/products/{productId}/options", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("상품 옵션 등록 API 테스트 - 유효성 검증 실패 (수량 범위 미만)")
    void registerProductOption_ValidationFail_QuantityBelowMinimum() throws Exception {
        // given
        Long productId = 1L;
        CreateProductOptionRequestDto dto = new CreateProductOptionRequestDto("옵션명", 0);

        // when & then
        mockMvc.perform(post("/api/products/{productId}/options", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("상품 옵션 조회 API 테스트")
    void getProductOptions_Success() throws Exception {
        // given
        Long productId = 1L;
        List<GetProductOptionResponseDto> options = Arrays.asList(
            new GetProductOptionResponseDto(1L, "옵션1", 100),
            new GetProductOptionResponseDto(2L, "옵션2", 200)
        );
        PageResponseDto<GetProductOptionResponseDto> pageResponse =
            new PageResponseDto<>(options, 2, 1, false, false);

        when(productOptionService.getProductOptions(eq(productId), any(Pageable.class)))
            .thenReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/api/products/{productId}/options", productId)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.content[0].name").value("옵션1"))
            .andExpect(jsonPath("$.content[1].name").value("옵션2"));
    }

    @Test
    @DisplayName("상품 옵션 삭제 API 테스트")
    void deleteProductOption_Success() throws Exception {
        // given
        Long optionId = 1L;

        // when & then
        mockMvc.perform(delete("/api/options/{optionId}", optionId))
            .andExpect(status().isNoContent());

        verify(productOptionService).deleteProductOption(optionId);
    }
}