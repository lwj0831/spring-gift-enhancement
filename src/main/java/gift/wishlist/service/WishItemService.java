package gift.wishlist.service;

import gift.member.domain.Member;
import gift.member.service.MemberService;
import gift.product.domain.Product;
import gift.product.service.ProductService;
import gift.wishlist.domain.WishItem;
import gift.wishlist.dto.GetWishItemResponseDto;
import gift.wishlist.dto.RegisterWishItemRequestDto;
import gift.wishlist.exception.WishItemAlreadyExistsException;
import gift.wishlist.exception.WishItemNotFoundException;
import gift.wishlist.repository.WishItemRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishItemService {

  private final WishItemRepository wishItemRepository;
  private final MemberService memberService;
  private final ProductService productService;

  public WishItemService(WishItemRepository wishItemRepository, MemberService memberService,
      ProductService productService) {
    this.wishItemRepository = wishItemRepository;
    this.memberService = memberService;
    this.productService = productService;
  }

  @Transactional
  public Long registerWishItem(Long memberId, RegisterWishItemRequestDto dto) {
    Member member = memberService.findMemberOrThrow(memberId);
    Product product = productService.findProductOrThrow(dto.productId());
    if (wishItemRepository.findByMemberIdAndProductId(member.id(), product.id()).isPresent()) {
      throw new WishItemAlreadyExistsException();
    }
    return wishItemRepository.save(WishItem.of(memberId, dto.productId()));
  }

  public List<GetWishItemResponseDto> findWishItems(Long memberId) {
    memberService.findMemberOrThrow(memberId);
    return wishItemRepository.findWishItemsWithProductByMemberId(memberId).stream()
        .map(GetWishItemResponseDto::from)
        .toList();
  }

  @Transactional
  public void deleteWishItem(Long id) {
    findWishItemOrThrow(id);
    wishItemRepository.deleteById(id);
  }

  public WishItem findWishItemOrThrow(Long id) {
    return wishItemRepository.findById(id).orElseThrow(WishItemNotFoundException::new);
  }
}
