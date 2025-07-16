package gift.wishlist.repository;

import gift.wishlist.dto.SimpleWishItemDto;
import gift.wishlist.jpa.WishItemEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaWishItemRepository extends JpaRepository<WishItemEntity, Long> {

  @Query("select we from WishItemEntity we join fetch we.member join fetch we.product where we.member.id = :memberId and we.product.id = :productId")
  Optional<WishItemEntity> findByMemberIdAndProductId(@Param("memberId") Long memberId,
      @Param("productId") Long productId);

  @Query("select we from WishItemEntity we join fetch we.member join fetch we.product where we.member.id = :memberId")
  List<WishItemEntity> findAllByMemberId(@Param("memberId") Long memberId);

  @Query("SELECT new gift.wishlist.dto.SimpleWishItemDto(pe.id, pe.name, pe.price, pe.imageUrl) FROM WishItemEntity we INNER JOIN ProductEntity pe ON we.product.id = pe.id WHERE we.member.id = :memberId")
  List<SimpleWishItemDto> findWishItemsWithProductByMemberId(@Param("memberId") Long memberId);

  Page<WishItemEntity> findAllPageByMemberId(Long memberId, Pageable pageable);
}
