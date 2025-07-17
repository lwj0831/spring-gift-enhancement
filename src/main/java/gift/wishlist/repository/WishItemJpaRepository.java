package gift.wishlist.repository;

import gift.wishlist.domain.WishItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WishItemJpaRepository extends JpaRepository<WishItem, Long> {

    @Query("select we from WishItem we where we.member.id = :memberId and we.product.id = :productId")
    Optional<WishItem> findByMemberIdAndProductId(@Param("memberId") Long memberId,
        @Param("productId") Long productId);

    @Query("select we from WishItem we join fetch we.product where we.member.id = :memberId")
    List<WishItem> findAllWithProductByMemberId(@Param("memberId") Long memberId);

}
