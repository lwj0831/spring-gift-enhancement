package gift.product.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private int price;
    private String description;
    private String imageUrl;

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<ProductOption> options = new ArrayList<>();

    protected Product() {
    }

    public void addOption(ProductOption productOption) {
        options.add(productOption);
        productOption.setProduct(this);
    }

    private Product(Long id, String name, int price, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public static Product of(String name, int price, String description, String imageUrl) {
        return new Product(null, name, price, description, imageUrl);
    }

    public static Product withId(Long id, String name, int price, String description,
        String imageUrl) {
        return new Product(id, name, price, description, imageUrl);
    }

    public void update(String name, int price, String description,
        String imageUrl) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<ProductOption> getOptions() {
        return options;
    }
}
