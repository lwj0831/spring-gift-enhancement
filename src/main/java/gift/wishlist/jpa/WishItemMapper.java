package gift.wishlist.jpa;

import gift.member.domain.Member;
import gift.member.jpa.MemberEntity;
import gift.member.jpa.MemberMapper;
import gift.product.domain.Product;
import gift.product.jpa.ProductEntity;
import gift.product.jpa.ProductMapper;
import gift.wishlist.domain.WishItem;

public class WishItemMapper {

    public static WishItem toDomain(WishItemEntity entity) {
        Member member = MemberMapper.toDomain(entity.getMember());
        Product product = ProductMapper.toDomain(entity.getProduct());
        return WishItem.withId(entity.getId(), member, product);
    }

    public static WishItemEntity toEntity(WishItem wishItem) {
        MemberEntity memberEntity = MemberMapper.toEntity(wishItem.member());
        ProductEntity productEntity = ProductMapper.toEntity(wishItem.product());
        return new WishItemEntity(wishItem.id(), memberEntity, productEntity);
    }

}
