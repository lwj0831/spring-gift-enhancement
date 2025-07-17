package gift.wishlist.jpa;

import gift.member.jpa.MemberEntity;
import gift.product.jpa.ProductEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "wish_item")
public class WishItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    public WishItemEntity() {
    }

    public WishItemEntity(Long id, MemberEntity member, ProductEntity product) {
        this.id = id;
        this.member = member;
        this.product = product;
    }

    public void update(MemberEntity member, ProductEntity product) {
        this.member = member;
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public MemberEntity getMember() {
        return member;
    }

    public ProductEntity getProduct() {
        return product;
    }
}
