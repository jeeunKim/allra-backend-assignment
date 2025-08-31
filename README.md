지원자: **김제은**
<br>휴대폰: 010-8752-8309
<br>이메일: wpdms991229@naver.com

<br>API TEST URL: http://{IP}:{PORT}/swagger-ui/index.html

<br>원래 과제 규칙상 각 기능별 Pull Request마다 AI와의 논의 기록을 남겨야 했으나, 결제 API 구현 전까지는 별도로 모아두고 있었습니다.
대신 모아둔 기능별 개발 과정과 AI와의 논의 내용을 README 하단 정리하였습니다.
<br>부주의로 인한 점 양해 부탁드립니다. 
<br>감사합니다.



# 📦 올라 마켓 백엔드 시스템 구축 프로젝트


## 📌 프로젝트 개요
이 프로젝트는 **상품 조회, 장바구니, 주문 및 결제 기능**을 제공하는 시스템입니다.

## ⚙️ 기술 스택
- **Language & Framework**
  - Java 17
  - Spring Boot 3.3.6
- **Database**
  - MySQL 8.0.43
- **ORM**
  - Spring Data JPA
- **API 규격**
  - Swagger-UI
- **빌드**
  - Maven


## 🛠 주요 기능
- **상품 목록 조회**
  - 조건 검색 (카테고리, 가격 범위, 이름 등)
  - 품절 여부 포함
  - 페이징 처리
  - 캐싱 적용 (`@Cacheable`)
- **장바구니 추가**
  - 장바구니에 상품 추가(이미 존재 시 수량 변경)
  - 재고 및 품절 여부 확인 
- **장바구니 조회**
  - 장바구니 내에 모든 상품 조회
  - 카테고리 별 그룹화
  - 품절 여부 포함
- **장바구니 수정**
  - 장바구니의 수량 조정 +, -
  - 마이너스 시 담은 수량이 0이 될 경우 제거
 - **장바구니 삭제**
   - 장바구니내에 특정 상품 제거
- **주문 결제**
  - Mock 결제 API 연동
  - 상품 재고 관리 및 동시성 문제 차단
  - 트랜잭션 설계 및 비관적 락 적용
  - 응답 결과에 따른 주문 상태 업데이트 및 재고 복원 또는 장바구니 정리
  - 주문, 주문 상세, 결제 이력 저장
  - 상품 목록 조회 캐싱 초기화 (`@CacheEvict`)
  

## 📑 API 주요 엔드포인트
### 상품 목록 조회 API
```http
GET /api/items?categoryId={categoryId}&detailCategoryId={detailCategoryId}&itemName={itemName}&minAmount={minAmount}&maxAmount={maxAmount}&page=0&size=10
```
### 장바구니 추가 API
```http
POST /api/cart
```
### 장바구니 조회 API
```http
GET /api/cart/{userId}
```

### 장바구니 수정 API
```http
PATCH /api/cart/{userId}/{itemId}
```
### 장바구니 삭제 API
```http
DELETE /api/cart/{userId}/{itemId}
```
### 주문 결제 API
```http
POST /api/payment
```

## 🚀 프로젝트 클론
git clone https://github.com/jeeunKim/allra-backend-assignment.git

## 📂 프로젝트 구조
 📂src
 └─  📂main
    ├─  📂java
    │   └─  📂com.allra.assignment
    │        ├─  📂config -- 설정 파일
    │        ├─  📂dev	   
    │        ├─  ├─📂cart   -- 장바구니 
    │        ├─  ├─📂item   -- 상품
    │        ├─  ├─📂order  -- 주문
    │        ├─  ├─📂user   -- 유저
    │        └─  📂exception -- 예외 처리
    └─  📂resources
    │	├─ application.yaml
    │	├─ application-local.yaml







# 상품 목록 조회 API

## 해결하려던 문제
상품 목록 조회 API에서 페이징, 정렬, 가격 범위, 카테고리 등 다양한 조건을 고려하여  
조회 기능을 구현하면서 테스트 코드 작성과 DB 설계 최적화를 고민했다.

## 대화 요약
### Q: 가격은 너무 천차만별이라 인덱스를 잡기는 좀 그런가?  
**A:** 가격은 범위 검색이 많지만 selectivity가 낮기 때문에 단일 인덱스는 효율이 낮다고 설명함.  
사용자는 이해하고, 카테고리나 조합 컬럼에 인덱스를 잡는 방향으로 수용.

### Q: 실무적인 관점에서 카테고리 PK를 auto increment가 좋을까 varchar로 하는게 좋을까?  
**A:** 일반적으로 성능과 외래키 연동 편의 때문에 `auto increment` 숫자형이 유리하다고 안내.  
사용자는 숫자형으로 설계 결정.

### Q: 이번 프로젝트는 테스트 코드로 API를 만들어보고 싶은데, 어떤 식으로 하는 건지 예시와 설명을 해줘. (Java17, Spring Boot 3.x)  
**A:** 테스트 코드 작성 목적과 범위 설명: 단위 테스트는 서비스 로직 검증, 통합 테스트는 API 호출 검증에 사용. Mockito로 Repository Mock, assertThat으로 결과 검증 예시 제안.  
사용자는 단위 테스트로 서비스 레이어 로직 검증에 초점을 맞추기로 함.

### Q: 이렇게 결과를 내가 임의로 주고 그걸 반환하게 해서 반환한 게 맞다고 하는 게 맞는 거야?  
**A:** Mockito의 given().willReturn()을 통해 Repository가 반환하는 데이터를 지정하여, 서비스 로직이 올바르게 동작하는지 검증 가능.  
사용자는 이 방법으로 실제 DB 접근 없이 서비스 로직만 테스트.

### Q: 페이징 sort 에러 예시 테스트 코드 보여줘  
**A:** Pageable에 잘못된 Sort 필드를 넣고, assertThatThrownBy로 예외 발생 확인. InvalidDataAccessApiUsageException을 검증하는 테스트 예시 제공.  
사용자는 이를 기반으로 올바른 예외 검증 테스트 작성.

### Q: InvalidDataAccessApiUsageException을 ItemException으로 잡으려면 어떻게 해야 해?  
**A:** 서비스 레이어에서 try-catch로 감싸거나 @ControllerAdvice로 전역 처리.  
사용자는 전역 처리기를 적용하여 서비스 레이어 코드는 깔끔하게 유지하고, 테스트에서 예외를 검증하도록 설계.

### Q: 근데 저 조회 구문에 InvalidDataAccessApiUsageException 하나 잡으려고 try-catch는 과하지 않아? 
**A:** 실제로는 전역 처리기로 충분하며, 서비스 레이어에서 직접 잡는 것은 과도할 수 있음.  
사용자는 전역 처리기 적용 후 테스트 코드에서도 동일한 예외를 검증.

### Q: 단위로 하고 싶은데, item의 필드명과 비교하면서 검증하거나 그렇게는 못하나?  
**A:** 단위 테스트에서는 실제 DB에 의존하지 않고, Mock 객체나 Reflection 등을 통해 필드명을 검증할 수 있다고 안내.  
사용자는 단위 테스트 목적에 맞게 Mocking 활용을 고려.

### Q: 테스트코드에서 올바르지 않은 정렬 필드 예외를 확인하려면?  
**A:** Pageable의 sort에 존재하지 않는 필드를 넣으면 `InvalidDataAccessApiUsageException`이 발생하며,  
전역 예외 처리기에서 ItemException으로 변환하는 전략을 권장. 테스트 코드에서는 `.isInstanceOf(InvalidDataAccessApiUsageException.class)`로 검증.

## 최종 적용 결과
- 상품 조회 API에서 min/max 가격, 카테고리, 이름 조건 검증 로직 구현  
- Pageable, Sort 검증 시 잘못된 필드 입력에 대한 전역 예외 처리 적용  
- DB 인덱스는 카테고리 + 상세 카테고리 + 정렬 컬럼 기준으로 설계  
- 테스트 코드는 Mock repository를 활용한 단위 테스트 작성  
- Service 레이어: ItemService.getItems에서 잘못된 정렬 필드, 가격 범위 이상 등의 예외를 전역 처리기로 관리  
- 테스트 코드:  
  - Pageable과 Sort를 잘못 넣었을 때 InvalidDataAccessApiUsageException 발생 확인  
  - Mockito로 Repository Mock 사용, 단위 테스트로 서비스 로직 검증  
  - 테스트를 통해 예외 상황과 정상 동작 모두 검증 가능




# 장바구니 추가 API


## 해결하려던 문제
장바구니에 상품을 추가하는 API를 구현하면서,  
중복 상품 처리, 수량 조정, 품절 여부, 재고 체크 등 다양한 예외 상황을 처리하고, 테스트 코드를 통해 검증하고자 함.

## 대화 요약
### Q: 장바구니 등록 API를 만들 때 사용자 편의적으로 예외처리해야 되는 거 또 뭐가 있을까?  
**A:** 주요 예외: 인증/인가, 상품 검증, 수량 관련, 장바구니 상태 관련, 데이터 무결성, 시스템/서버 예외  
사용자는 이 예외들을 커스텀 Exception과 ErrorResult로 정의하고 처리.

### Q: 품절 상품은 장바구니에 담을 수 없게 하는 로직을 테스트코드를 어떻게 짜야 할까?  
**A:** Mockito를 이용해 ItemRepository와 UserRepository를 Mock하고, Item.isSoldOut=true인 경우 CartItemService.addItemToCart 호출 시 ItemException 발생을 assert.  
사용자는 이를 테스트 코드로 작성하고 UnnecessaryStubbingException 방지를 위해 lenient 설정을 고려.

### Q: 장바구니 수량 조정 API에서 + / - 버튼 구현할 때 테스트코드는 어떻게 짜야 할까?  
**A:** increment와 decrement 메서드를 각각 테스트, 재고 부족과 품절 예외 케이스를 별도로 검증하는 단위 테스트 제안.  
사용자는 각각의 예외 및 정상 케이스를 Mockito와 assertThatThrownBy로 검증.

## 최종 적용 결과
- CartItemService.addItemToCart 구현:  
  - 기존 장바구니 상품이 있으면 수량 조정  
  - 최초 추가 시 Item, User 존재 여부 검증 후 저장  
  - 품절, 재고 부족, 중복 상품 등 예외 처리  
- 테스트 코드 작성:  
  - Mockito로 Repository Mock  
  - 품절, 재고 부족, 사용자 미존재, 상품 미존재 등 케이스 검증  
  - increment/decrement 기능에 대한 단위 테스트 완료  


# 장바구니 조회 API

## 해결하려던 문제
장바구니 조회 API에서 사용자 친화적이고 실무적인 DTO 설계, 가격 비교, 카테고리 그룹화, 품절 처리 등을 어떻게 구현할지 고민.

## 대화 요약
### Q: 장바구니 목록을 조회하는 API의 어떤 응답이 사용자 친화적일까?  
**A:** 고객 입장에서 장바구니에 담은 당시 가격과 현재 가격을 함께 보여주고, 품절 여부, 상품 링크 등을 포함하면 UX 친화적.  
사용자는 DTO 설계에 `amountAtAdded`, `amountNow`, `itemLocation` 등을 포함하기로 결정.

### Q: cartItemRepository.findByUserUserId(userId).stream().collect(Collectors.groupingBy()) … cartItem에서 그룹핑과 DTO 변환을 동시에 하기
**A:** Stream API와 Collectors.groupingBy를 사용해 상위 카테고리 ID 기준으로 그룹화하고, Collectors.mapping으로 CartItem → DTO 변환 가능.  
사용자는 Map<Integer, List<MyItemDto>> 형태로 상위 카테고리별 그룹화를 적용.

### Q: 좀 더 쉽고 짧게 하는 방법 알려줘  
**A:** Stream API에서 groupingBy + mapping을 한 번에 사용하여 간결하게 처리.  
사용자는 기존 복잡한 for-loop를 제거하고 Stream 기반으로 DTO 변환 및 그룹화를 간소화.

## 최종 적용 결과
- Service 레이어: `CartItemService.getCartItems(userId)`  
  - 사용자 장바구니 조회  
  - 품절 체크  
  - 상위 카테고리별 그룹화 (`Map<Integer, List<MyItemDto>>`)  
  - 담았을 당시 가격(`amountAtAdded`)과 현재 가격(`amountNow`) DTO로 분리  
  - 상품 링크(`itemLocation`) 포함
- Stream API를 활용한 간결한 그룹화 및 매핑 로직 구현



# 주문 결제 API

## 해결하려던 문제
쇼핑몰 주문/결제 API 구현 중, **장바구니 내 상품 재고 관리, 외부 결제 연동, 트랜잭션 처리, 비관적 락 적용, 결제 실패 시 보상 처리** 등을 안정적으로 구현하는 방법과 구조를 설계하고자 함.

## 대화 요약

### Q: RestTemplate은 멀티 쓰레드, blocking방식이고, WebClient는 싱글 쓰레드, non-blocking 형식이잖아? 쇼핑몰에 실시간 결제 시스템을 구현할때 어떤 방식이 적합하다고 보여져?
**A:** 결제처럼 **즉시 트랜잭션 완료가 필요한 경우**에는 blocking 방식(RestTemplate)이 적합하며, WebClient는 대규모 동시 요청/응답 스트리밍에 유리.

### Q: 일반 지금 프로젝트가 WebFlux가 아니라 MVC기반이고, 결제 특성상 결제가 엄청 몰리는 상황보다는 결제를 진행하는 와중에 재고 관리하면서 같은 상품에 대한 다른 결제에 트랜잭션을 엄격하게 관리해야 할 것 같은데 그런 면에서 RestTemplate이 나을거 같은데 어떻게 생각해
**A:** MVC 기반에서 트랜잭션 관리와 재고 동시성 문제를 고려하면 **RestTemplate + blocking + 비관적 락**이 적절.

### Q: Beeceptor에서 한 API에 대해 성공 응답/실패 응답을 동시에 설정하는 방법
**A:** Beeceptor 자체에서는 Rule을 만들어 조건에 따라 성공/실패 응답을 반환하도록 구성 가능. 예: Header, Body 값에 따른 Conditional Response.

### Q: 결제하는동안 재고 관리를 진행해야하는데, Serializable 정도가 필요로 하려나? 다른 상품을 주문한다면 해당 상품의 재고는 상관이 없을텐데
**A:** -   **Serializable 전체 적용은 과도함. **Repeatable Read + Pessimistic Lock (`FOR UPDATE`)** 조합으로 충분.

### Q: 외부 결제 API 호출 동안 트랜잭션을 중단하는 이유
**A:** 결제 API 응답 지연 시 장기간 DB 락 점유 방지, 다른 상품 주문 처리 지연 방지, 같은 상품에 대해서만 비관적 락으로 대기.

### Q: 보상 트랜잭션은 왜 별도로 적용하는가?
**A:** 결제 실패 시 재고 복원 및 주문 상태 업데이트를 기존 주문 생성 트랜잭션에 영향을 주지 않고 안전하게 처리하기 위함.

## 최종 적용 결과
  - RestTemplate으로 적용
  - 사용자의 장바구니(cartItems)를 조회 후, 각 상품(Item)에 대해 비관적 락(PESSIMISTIC_WRITE) 적용.
  - 결제 API 호출 시 기존 주문 트랜잭션은 `NOT_SUPPORTED`로 중단.
  - 결제 결과 처리: 주문 트랜잭션 복귀 후 상태 업데이트 및 보상 처리.
- **핵심 이점**
  - 재고 동시성 문제 방지.
  - 외부 API 지연으로 인한 전체 트랜잭션 지연 방지.
  - 결제 실패 시 재고 자동 복원으로 데이터 일관성 유지.











