package gift.product.repository;

import gift.product.domain.ProductOption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductOptionJpaRepository extends JpaRepository<ProductOption, Long> {

    boolean existsByProductIdAndName(Long productId, String name);

    @Query("select po from ProductOption po where po.product.id = :productId")
    Page<ProductOption> findAllByProductId(@Param("productId") Long productId, Pageable pageable);

}
