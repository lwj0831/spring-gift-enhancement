package gift.wishlist.repository;

import gift.wishlist.domain.WishItem;
import gift.wishlist.dto.SimpleWishItemDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WishItemJpaRepository extends JpaRepository<WishItem, Long> {

    @Query("select we from WishItem we join fetch we.member join fetch we.product where we.member.id = :memberId and we.product.id = :productId")
    Optional<WishItem> findByMemberIdAndProductId(@Param("memberId") Long memberId,
        @Param("productId") Long productId);

    @Query("select we from WishItem we join fetch we.member join fetch we.product where we.member.id = :memberId")
    List<WishItem> findAllByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT new gift.wishlist.dto.SimpleWishItemDto(pe.id, pe.name, pe.price, pe.imageUrl) FROM WishItem we INNER JOIN Product pe ON we.product.id = pe.id WHERE we.member.id = :memberId")
    List<SimpleWishItemDto> findWishItemsWithProductByMemberId(@Param("memberId") Long memberId);

}
