package gift.auth.jpa;

import gift.auth.domain.MemberAuth;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "member_auth")
public class MemberAuthEntity {

  @Id
  private Long id;

  private String email;
  private String password;
  private String refreshToken;

  public MemberAuthEntity() {
  }

  public MemberAuthEntity(Long id, String email, String password, String refreshToken) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.refreshToken = refreshToken;
  }

  public void updateFromDomain(MemberAuth memberAuth) {
    this.id = memberAuth.memberId();
    this.email = memberAuth.email().getEmailText();
    this.password = memberAuth.password();
    ;
    this.refreshToken = memberAuth.refreshToken();
  }

  public void updateRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public String getRefreshToken() {
    return refreshToken;
  }
}
