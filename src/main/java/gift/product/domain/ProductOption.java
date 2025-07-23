package gift.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "product_option",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_product_option_product_id_name",
        columnNames = {"product_id", "name"}
    )
)
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private static final int QUANTITY_MIN_VALUE = 1;
    private static final int QUANTITY_MAX_VALUE = 100000000;

    protected ProductOption() {
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void addQuantity(int quantity) {
        int result = this.quantity + quantity;
        if (result >= QUANTITY_MAX_VALUE) {
            this.quantity = result;
        } else {
            throw new IllegalArgumentException("상품 수량 최대값(" + QUANTITY_MAX_VALUE + ") 초과입니다.");
        }
    }

    public void subtractQuantity(int quantity) {
        int result = this.quantity - quantity;
        if (result >= QUANTITY_MIN_VALUE) {
            this.quantity = result;
        } else {
            throw new IllegalArgumentException("상품 수량이 부족합니다");
        }
    }

    private ProductOption(Long id, String name, int quantity, Product product) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.product = product;
    }

    public static ProductOption of(String name, int quantity) {
        return new ProductOption(null, name, quantity, null);
    }

    public static ProductOption of(String name, int quantity, Product product) {
        return new ProductOption(null, name, quantity, product);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }
}
