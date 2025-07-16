package gift.member.jpa;

import gift.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "member")
public class MemberEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  public MemberEntity() {
  }

  public MemberEntity(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public void updateFromDomain(Member member) {
    this.id = member.id();
    this.name = member.name();
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
