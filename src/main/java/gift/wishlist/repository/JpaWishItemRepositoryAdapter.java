package gift.wishlist.repository;

import gift.global.common.dto.SortInfo;
import gift.member.jpa.MemberMapper;
import gift.product.jpa.ProductMapper;
import gift.wishlist.domain.WishItem;
import gift.wishlist.dto.SimpleWishItemDto;
import gift.wishlist.jpa.WishItemEntity;
import gift.wishlist.jpa.WishItemMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

public class JpaWishItemRepositoryAdapter implements WishItemRepository {

  private final JpaWishItemRepository jpaWishItemRepository;

  public JpaWishItemRepositoryAdapter(JpaWishItemRepository jpaWishItemRepository) {
    this.jpaWishItemRepository = jpaWishItemRepository;
  }

  @Override
  public Long save(WishItem wishItem) {
    WishItemEntity wishItemEntity = jpaWishItemRepository.save(WishItemMapper.toEntity(wishItem));
    return wishItemEntity.getId();
  }

  @Override
  public Optional<WishItem> findById(Long id) {
    return jpaWishItemRepository.findById(id).map(WishItemMapper::toDomain);
  }

  @Override
  public Optional<WishItem> findByMemberIdAndProductId(Long memberId, Long productId) {
    return jpaWishItemRepository.findByMemberIdAndProductId(memberId, productId)
        .map(WishItemMapper::toDomain);
  }

  @Override
  public List<SimpleWishItemDto> findWishItemsWithProductByMemberId(Long memberId) {
    return jpaWishItemRepository.findWishItemsWithProductByMemberId(memberId);
  }

  @Override
  public List<WishItem> findAllByMemberId(Long memberId) {
    return jpaWishItemRepository.findAllByMemberId(memberId).stream()
        .map(WishItemMapper::toDomain)
        .toList();
  }

  @Override
  public List<WishItem> findAllByPage(int offset, int pageSize, SortInfo sortInfo, Long memberId) {
    Direction direction = sortInfo.isAscending() ? Direction.ASC : Direction.DESC;
    PageRequest pageRequest = PageRequest.of(offset, pageSize, direction, sortInfo.field());
    Page<WishItemEntity> results = jpaWishItemRepository.findAllPageByMemberId(memberId,
        pageRequest);
    return results.stream()
        .map(p -> WishItem.withId(p.getId(), MemberMapper.toDomain(p.getMember()),
            ProductMapper.toDomain(p.getProduct())))
        .toList();
  }

  @Override
  public void deleteById(Long id) {
    jpaWishItemRepository.deleteById(id);
  }
}
