# 🧱 1단계 - 엔티티 매핑

해당 단계는 기존 JDBC 기반 도메인 구조를 유지하면서, JPA 기반의 Repository와 Entity를 새로 도입하여 유연하고 확장 가능한 구조로 전환하는 작업을 수행한
단계입니다.  
각 도메인에 대해 Entity와 Mapper를 분리하고, Repository는 프로파일에 따라 JPA 또는 JDBC를 선택적으로 주입할 수 있도록 구성했습니다.

---

## ✅ 구현 사항

### 📌 1. WishItem, Member, Product 도메인에 대한 JPA 기반 Entity/Repository 구현

- [x] 각 도메인에 대한 `Entity` 및 `JpaRepository` 구현
- [x] 기존 도메인 객체와 엔티티 객체를 분리하고, 변환을 위한 Mapper 클래스 도입
- [x] WishItem → Member, Product 간의 연관 관계를 객체 참조 기반으로 설정 (`@ManyToOne`)
- [x] N+1 문제 방지를 위한 `fetch join` 기반 조회 쿼리 작성
- [x] DTO 직접 조회 방식(`SimpleWishItemDto`)으로 성능 최적화

### 📌 2. 프로파일 기반 Repository 및 DB 설정 분리

- [x] `dev` : JPA + MySQL
- [x] `test` : JPA + H2 (In-memory)
- [x] `default` : JDBC + H2 (In-memory)
- [x] `@Profile` 기반으로 Repository 빈 주입 및 datasource 초기화 전략 분리
- [x] `schema.sql`, `data.sql` 등 초기화 SQL 스크립트 환경별 분리

### 📌 3. 기타 기술적 해결 사항

- [x] `JpaWishItemRepositoryImpl` → `JpaWishItemRepositoryAdapter`로 네이밍 변경하여 빈 중복 문제 해결
- [x] Spring Data JPA의 자동 구현 클래스 탐지 및 빈 등록 원리에 대한 학습 및 대응

---

## 🚀 향후 확장 계획

- [ ] 각 도메인에 대한 **JPA 기반 단위 테스트** 추가
- [ ] 생성/수정 시각을 관리하는 **TimeBaseEntity** 도입 및 공통 추상 엔티티 구성
- [ ] `Member`, `WishItem` 등의 엔티티에 `@OneToMany`, `cascade`, `orphanRemoval` 등 영속성 전이 로직 적용
- [ ] Mapper 클래스의 단순 변환 로직을 MapStruct 등으로 리팩토링
- [ ] `fetch join`과 DTO 조회 방식을 정리하여 도메인별 조회 성격에 따라 전략화
