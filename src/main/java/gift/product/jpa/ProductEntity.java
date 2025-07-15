package gift.product.jpa;

import gift.product.domain.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class ProductEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "product_id")
  private Long id;
  private String name;
  private int price;
  private String description;
  private String imageUrl;

  public ProductEntity(){
  }

  public ProductEntity(Long id, String name, int price, String description, String imageUrl) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.description = description;
    this.imageUrl = imageUrl;
  }

  public void updateFromDomain(Product product){
    this.name = product.name();
    this.price = product.price();
    this.description = product.description();
    this.imageUrl = product.imageUrl();
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
}
