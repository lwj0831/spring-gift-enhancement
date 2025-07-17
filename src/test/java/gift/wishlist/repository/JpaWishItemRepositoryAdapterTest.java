package gift.wishlist.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.global.common.dto.SortInfo;
import gift.member.domain.Member;
import gift.member.jpa.MemberEntity;
import gift.member.jpa.MemberMapper;
import gift.product.domain.Product;
import gift.product.jpa.ProductEntity;
import gift.product.jpa.ProductMapper;
import gift.wishlist.domain.WishItem;
import gift.wishlist.dto.SimpleWishItemDto;
import gift.wishlist.jpa.WishItemEntity;
import gift.wishlist.jpa.WishItemMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class JpaWishItemRepositoryAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JpaWishItemRepository jpaWishItemRepository;

    private JpaWishItemRepositoryAdapter wishItemRepositoryAdapter;

    private MemberEntity testMember1;
    private MemberEntity testMember2;
    private ProductEntity testProduct1;
    private ProductEntity testProduct2;

    @BeforeEach
    void setUp() {
        wishItemRepositoryAdapter = new JpaWishItemRepositoryAdapter(jpaWishItemRepository);
        setupTestData();
    }

    private void setupTestData() {
        testMember1 = createAndSaveMemberEntity("사용자1");
        testMember2 = createAndSaveMemberEntity("사용자2");
        testProduct1 = createAndSaveProductEntity("상품1", 10000, "상품1 설명", "http://test.image.com");
        testProduct2 = createAndSaveProductEntity("상품2", 20000, "상품2 설명", "http://test.image.com");
    }

    @Test
    @DisplayName("위시리스트 아이템을 저장하고 ID를 반환한다")
    void save_ShouldReturnId() {
        WishItem wishItem = createWishItem(testMember1, testProduct1);

        Long savedId = wishItemRepositoryAdapter.save(wishItem);

        WishItemEntity savedEntity = entityManager.find(WishItemEntity.class, savedId);

        assertAll(
            () -> assertThat(savedId).isNotNull(),
            () -> assertThat(savedId).isGreaterThan(0),
            () -> assertThat(savedEntity).isNotNull(),
            () -> assertThat(savedEntity.getMember().getId()).isEqualTo(testMember1.getId()),
            () -> assertThat(savedEntity.getProduct().getId()).isEqualTo(testProduct1.getId())
        );
    }

    @Test
    @DisplayName("ID로 위시리스트 아이템을 조회한다")
    void findById_ShouldReturnWishItem() {
        WishItemEntity savedEntity = createAndSaveWishItemEntity(testMember1, testProduct1);

        Optional<WishItem> foundWishItem = wishItemRepositoryAdapter.findById(savedEntity.getId());

        assertAll(
            () -> assertThat(foundWishItem).isPresent(),
            () -> assertThat(foundWishItem.get().member().id()).isEqualTo(testMember1.getId()),
            () -> assertThat(foundWishItem.get().product().id()).isEqualTo(testProduct1.getId())
        );
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회시 빈 Optional을 반환한다")
    void findById_WithNonExistentId_ShouldReturnEmptyOptional() {
        Long nonExistentId = 999L;

        Optional<WishItem> foundWishItem = wishItemRepositoryAdapter.findById(nonExistentId);

        assertThat(foundWishItem).isEmpty();
    }

    @Test
    @DisplayName("멤버ID와 상품ID로 위시리스트 아이템을 조회한다")
    void findByMemberIdAndProductId_ShouldReturnWishItem() {
        createAndSaveWishItemEntity(testMember1, testProduct1);

        Optional<WishItem> foundWishItem = wishItemRepositoryAdapter.findByMemberIdAndProductId(
            testMember1.getId(), testProduct1.getId());

        assertAll(
            () -> assertThat(foundWishItem).isPresent(),
            () -> assertThat(foundWishItem.get().member().id()).isEqualTo(testMember1.getId()),
            () -> assertThat(foundWishItem.get().product().id()).isEqualTo(testProduct1.getId())
        );
    }

    @Test
    @DisplayName("멤버ID와 상품ID로 조회시 존재하지 않으면 빈 Optional을 반환한다")
    void findByMemberIdAndProductId_WithNonExistentData_ShouldReturnEmptyOptional() {
        Optional<WishItem> foundWishItem = wishItemRepositoryAdapter.findByMemberIdAndProductId(
            testMember1.getId(), testProduct1.getId());

        assertThat(foundWishItem).isEmpty();
    }

    @Test
    @DisplayName("멤버ID로 위시리스트 아이템을 상품 정보와 함께 조회한다")
    void findWishItemsWithProductByMemberId_ShouldReturnWishItemsWithProduct() {
        createAndSaveWishItemEntity(testMember1, testProduct1);
        createAndSaveWishItemEntity(testMember1, testProduct2);
        createAndSaveWishItemEntity(testMember2, testProduct1);

        List<SimpleWishItemDto> wishItems = wishItemRepositoryAdapter
            .findWishItemsWithProductByMemberId(testMember1.getId());

        assertAll(
            () -> assertThat(wishItems).hasSize(2),
            () -> assertThat(wishItems.get(0).name()).isEqualTo("상품1"),
            () -> assertThat(wishItems.get(1).name()).isEqualTo("상품2")
        );
    }

    @Test
    @DisplayName("멤버ID로 모든 위시리스트 아이템을 조회한다")
    void findAllByMemberId_ShouldReturnAllWishItemsForMember() {
        createAndSaveWishItemEntity(testMember1, testProduct1);
        createAndSaveWishItemEntity(testMember1, testProduct2);
        createAndSaveWishItemEntity(testMember2, testProduct1);

        List<WishItem> wishItems = wishItemRepositoryAdapter.findAllByMemberId(testMember1.getId());

        assertAll(
            () -> assertThat(wishItems).hasSize(2),
            () -> assertThat(wishItems)
                .extracting(wishItem -> wishItem.member().id())
                .containsOnly(testMember1.getId())
        );
    }

    @Test
    @DisplayName("페이지네이션과 정렬을 적용하여 위시리스트 아이템을 조회한다")
    void findAllByPage_ShouldReturnPagedAndSortedWishItems() {
        createAndSaveWishItemEntity(testMember1, testProduct1);
        createAndSaveWishItemEntity(testMember1, testProduct2);

        SortInfo sortInfo = new SortInfo("id", true);

        List<WishItem> wishItems = wishItemRepositoryAdapter.findAllByPage(
            0, 10, sortInfo, testMember1.getId());

        assertAll(
            () -> assertThat(wishItems).hasSize(2),
            () -> assertThat(wishItems)
                .extracting(wishItem -> wishItem.member().id())
                .containsOnly(testMember1.getId())
        );
    }

    @Test
    @DisplayName("페이지네이션으로 첫 번째 페이지만 조회한다")
    void findAllByPage_WithPagination_ShouldReturnFirstPageOnly() {
        createAndSaveWishItemEntity(testMember1, testProduct1);
        createAndSaveWishItemEntity(testMember1, testProduct2);

        SortInfo sortInfo = new SortInfo("id", true);

        List<WishItem> wishItems = wishItemRepositoryAdapter.findAllByPage(
            0, 1, sortInfo, testMember1.getId());

        assertThat(wishItems).hasSize(1);
    }

    @Test
    @DisplayName("위시리스트 아이템을 삭제한다")
    void deleteById_ShouldDeleteWishItem() {
        WishItemEntity savedEntity = createAndSaveWishItemEntity(testMember1, testProduct1);
        Long wishItemId = savedEntity.getId();

        wishItemRepositoryAdapter.deleteById(wishItemId);
        entityManager.flush();

        WishItemEntity deletedEntity = entityManager.find(WishItemEntity.class, wishItemId);
        assertThat(deletedEntity).isNull();
    }

    @Test
    @DisplayName("존재하지 않는 위시리스트 아이템을 삭제해도 예외가 발생하지 않는다")
    void deleteById_WithNonExistentId_ShouldNotThrowException() {
        Long nonExistentId = 999L;

        wishItemRepositoryAdapter.deleteById(nonExistentId);
    }

    private WishItem createWishItem(MemberEntity memberEntity, ProductEntity productEntity) {
        Member member = MemberMapper.toDomain(memberEntity);
        Product product = ProductMapper.toDomain(productEntity);
        return WishItem.of(member, product);
    }

    private WishItemEntity createAndSaveWishItemEntity(MemberEntity memberEntity,
        ProductEntity productEntity) {
        WishItem wishItem = createWishItem(memberEntity, productEntity);
        WishItemEntity entity = WishItemMapper.toEntity(wishItem);
        entity.update(memberEntity, productEntity);
        return entityManager.persistAndFlush(entity);
    }

    private MemberEntity createAndSaveMemberEntity(String name) {
        Member member = Member.of(name);
        MemberEntity entity = MemberMapper.toEntity(member);
        return entityManager.persistAndFlush(entity);
    }

    private ProductEntity createAndSaveProductEntity(String name, int price, String description,
        String imageUrl) {
        Product product = Product.of(name, price, description, imageUrl);
        ProductEntity entity = ProductMapper.toEntity(product);
        return entityManager.persistAndFlush(entity);
    }
}
