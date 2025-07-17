package gift.wishlist.repository;

import gift.global.common.dto.SortInfo;
import gift.wishlist.domain.WishItem;
import gift.wishlist.dto.SimpleWishItemDto;
import java.util.List;
import java.util.Optional;

public interface WishItemRepository {

    Long save(WishItem wishItem);

    Optional<WishItem> findById(Long id);

    Optional<WishItem> findByMemberIdAndProductId(Long memberId, Long productId);

    List<SimpleWishItemDto> findWishItemsWithProductByMemberId(Long memberId);

    List<WishItem> findAllByMemberId(Long memberId);

    List<WishItem> findAllByPage(int offset, int pageSize, SortInfo sortInfo, Long memberId);

    void deleteById(Long id);

}
