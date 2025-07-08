# Marketbill Core Server 구조 및 GraphQL API 기능 안내

MarketBill은 화훼 시장의 경매와 거래를 디지털화하는 플랫폼입니다. 이 프로젝트는 MarketBill의 코어 백엔드 Kotlin + Spring Boot 서버를 구현합니다.

## 예시 화면
| ![](https://github.com/user-attachments/assets/633740fb-1d10-493a-8521-7d33f618f6f9) | ![](https://github.com/user-attachments/assets/7088a9a0-3e7f-4b96-b0d3-54a80b1cecec) | ![](https://github.com/user-attachments/assets/f0380ba1-2e84-4e1b-a849-276b83275266) |
|:---:|:---:|:---:|
| ![](https://github.com/user-attachments/assets/c1eb25d4-87d1-4498-ada9-3df790ad1940) | ![](https://github.com/user-attachments/assets/45886e65-f458-48f9-899f-88ef9a31e197) | ![](https://github.com/user-attachments/assets/a8aeece4-190c-4963-9747-0ead98470563) |
| ![](https://github.com/user-attachments/assets/50590179-d185-45ff-a1cf-67426d4dcd9e) |  |  |

## 기술 스택

- **언어**: Kotlin
- **프레임워크**: Spring Boot
- **API**: GraphQL (Netflix DGS Framework)
- **보안**: Spring Security, JWT
- **데이터베이스**: JPA/Hibernate
- **외부 서비스 연동**: 
  - SMS 메시징 서비스
  - 파일 처리 서비스


## 폴더 구조 및 계층 설명

본 프로젝트는 **Hexagonal Architecture(Ports & Adapters)** 기반으로, 도메인 중심 설계와 OOP 원칙을 철저히 준수합니다. 각 도메인은 독립적으로 유지보수 및 확장이 가능하도록 계층화되어 있으며, 비즈니스 로직과 인프라스트럭처의 결합을 최소화하였습니다.

아래는 실제 폴더 구조 예시입니다:

`main`
```
├── MarketbillCoreServerApplication.kt           # 서버 메인 엔트리포인트
├── cart                                        # 장바구니 도메인 (아래 구조는 flower/order/user도 유사)
│   ├── adapter                                 # 입출력/외부 시스템 연동 계층
│   │   ├── in                                  # 외부 요청 수신(예: GraphQL)
│   │   │   └── graphql                         # GraphQL 관련 어댑터
│   │   │       ├── context                     # 요청별 컨텍스트 관리
│   │   │       ├── datafetcher                 # 쿼리/뮤테이션 데이터 패처
│   │   │       ├── dataloader                  # 배치 데이터 로더
│   │   │       └── mapper                      # 응답 변환 매퍼
│   │   └── out                                 # 외부 시스템 연동
│   │       └── persistence                     # DB 등 영속성 계층
│   │           ├── entity                      # JPA 엔티티
│   │           ├── mapper                      # 엔티티-도메인 변환 매퍼
│   │           └── repository                  # 리포지토리 구현체
│   ├── application                             # 유스케이스, 서비스, 명령/결과 객체 등
│   │   ├── command                             # 유스케이스 실행용 명령 객체
│   │   ├── port                                # 포트(외부 의존성 인터페이스)
│   │   │   └── outbound                        # 아웃바운드 포트(리포지토리 등)
│   │   ├── result                              # 유스케이스 결과 객체
│   │   ├── service                             # 도메인 서비스(비즈니스 로직)
│   │   └── usecase                             # 주요 유스케이스 정의/구현
│   └── domain                                  # 도메인 모델 계층
│       ├── model                               # 엔티티/애그리거트(핵심 비즈니스 객체)
│       └── vo                                  # 값 객체(Value Object)
├── flower                                      # 꽃 도메인 (cart와 동일 구조, 설명 생략)
├── order                                       # 주문 도메인 (cart와 동일 구조, 설명 생략)
├── user                                        # 유저/거래처 도메인 (cart와 동일 구조, 설명 생략)
├── shared                                      # 공통 유틸리티/설정/인프라 등 전 도메인 공용
│   ├── adapter                                 # 공통 어댑터(인증/매퍼/스칼라 등)
│   │   ├── in                                  # 외부 요청 관련 공통 코드
│   │   │   ├── graphql                         # 공통 GraphQL 어댑터(컨텍스트, DTO, 매퍼 등)
│   │   │   └── security                        # 인증/보안 관련 필터 및 프로바이더
│   │   └── out                                 # 외부 시스템 연동 공통 코드
│   │       └── persistence                     # 공통 영속성(예: 사용자 인증 서비스, 타입 매퍼 등)
│   ├── application                             # 공통 서비스, 포트, 매퍼 등
│   ├── config                                  # 전역 설정(시큐리티, 스케줄러, 쿼리DSL 등)
│   ├── constant                                # 상수 정의
│   ├── domain                                  # 공통 도메인 모델/VO
│   ├── error                                   # 예외 및 에러코드
│   └── infrastructure                          # 공통 인프라 어댑터, 베이스 엔티티/매퍼 등

```
`resources`
```
├── application.yml             # 모든 환경에서 공통으로 사용하는 기본 설정 파일
├── data.sql                    # DB 초기화 및 개발/테스트용 기본 데이터 삽입 SQL
└── schema                      # GraphQL 스키마 정의 폴더(도메인별로 분리)
    ├── auction.graphql         # 경매 도메인 타입/쿼리/뮤테이션 스키마
    ├── cart.graphql            # 장바구니 도메인 타입/쿼리/뮤테이션 스키마
    ├── common.graphql          # 공통 타입 및 유틸리티 쿼리/뮤테이션 스키마
    ├── constants.graphql       # enum, scalar 등 전역 상수 및 타입 정의
    ├── dto.graphql             # 입력/출력 DTO 타입 정의(공통 Input/Output)
    ├── flower.graphql          # 꽃 도메인 타입/쿼리 스키마
    ├── order.graphql           # 주문 도메인 타입/쿼리/뮤테이션 스키마
    └── user.graphql            # 유저/거래처 도메인 타입/쿼리/뮤테이션 스키마

```
### 각 계층 역할

- **adapter/in/graphql**: GraphQL 쿼리/뮤테이션 요청 처리, 데이터 패처 등
- **adapter/out/persistence**: DB 등 외부 시스템 연동, 엔티티/리포지토리/매퍼
- **application**: 유스케이스, 서비스, 커맨드, 포트, 결과 객체 등 비즈니스 로직 계층
- **domain**: 도메인 엔티티, 애그리거트, 값 객체(Value Object)
- **shared**: 공통 유틸리티, 설정, 예외, 인증, 인프라 등

## 지원하는 GraphQL API 기능

아래는 실제 폴더 구조 및 스키마에 기반해, 각 도메인별로 제공하는 주요 **쿼리(Query)** 및 **뮤테이션(Mutation)** 기능과 그 역할을 설명합니다.

### 1. Cart(장바구니) 도메인

- **shoppingSession**  
  소매상의 장바구니 세션(담긴 품목, 거래처, 메모 등) 정보를 조회합니다.
- **addToCart**  
  장바구니에 새로운 꽃 품목을 추가합니다.
- **removeCartItem**  
  장바구니에서 특정 품목을 삭제합니다.
- **updateCartItem**  
  장바구니 품목의 수량, 등급, 메모 등을 수정합니다.
- **updateShoppingSession**  
  장바구니 세션의 거래처(도매상), 메모 등을 변경합니다.

### 2. Flower(꽃) 도메인

- **flowers**  
  기간별 또는 키워드 기반으로 꽃 목록을 조회합니다.  
  (예: 특정 기간에 경매된 꽃, 이름/색상 등으로 검색)

### 3. Auction(경매) 도메인

- **auctionResult**  
  도매상 기준 경매 결과 리스트를 조회합니다.
- **auctionResultDetail**  
  특정 경매 결과의 상세 정보를 조회합니다.
- **auctionResultForSale**  
  소매상 기준 경매 결과 리스트를 조회합니다.
- **auctionResultForSaleDetail**  
  소매상 기준 특정 경매 결과 상세 조회
- **updateAuctionResult**  
  도매상이 경매 매물의 판매가, 품절 여부 등을 업데이트합니다.

### 4. Order(주문) 도메인

- **orderSheets**  
  주문서 목록을 조회합니다.
- **orderSheet**  
  주문서 상세 정보를 조회합니다.
- **orderItems**  
  주문 항목(꽃 품목) 목록을 조회합니다.
- **dailyOrderSheetAggregates**  
  일자별로 주문 집계(꽃 종류, 주문 수 등)를 조회합니다.
- **dailyOrderSheetAggregate**  
  특정 일자의 주문 집계 정보를 조회합니다.
- **dailyOrderItems**  
  일자별 주문 항목(도매상 기준) 목록을 조회합니다.
- **orderCartItems**  
  장바구니에 담긴 품목들을 주문서로 생성합니다.
- **removeOrderSheet**  
  주문서를 삭제(주문 취소)합니다.
- **updateOrderItemsPrice**  
  주문 항목들의 판매가를 일괄 수정합니다.
- **upsertCustomOrderItems**  
  커스텀 주문 항목을 추가/수정합니다.
- **removeCustomOrderItem**  
  커스텀 주문 항목을 삭제합니다.
- **updateDailyOrderItemsPrice**  
  일자별 주문 항목 가격을 일괄 수정합니다.
- **issueOrderSheetReceipt**  
  주문서 영수증을 발행합니다.
- **orderBatchCartItems**  
  장바구니 항목을 일괄 주문 처리(스케줄러 등 자동화용)합니다.

### 5. User(유저/거래처) 도메인

- **me**  
  내 정보(프로필, 거래처 등) 조회
- **users**  
  유저 목록 조회(이름, 역할, 번호 등 필터링)
- **user**  
  단일 유저 상세 정보 조회
- **signUp**  
  회원가입(소매상/도매상/직원 등)
- **signIn**  
  로그인
- **applyBizConnection**  
  소매상이 도매상에 거래처 신청
- **updateBizConnection**  
  도매상이 소매상 거래처 신청을 승인/거절
- **signOut**  
  로그아웃
- **reissueToken**  
  토큰 재발급
- **removeUser**  
  유저 삭제(관리자)
- **upsertBusinessInfo**  
  업체 정보 등록/수정(관리자)
- **updatePassword**  
  비밀번호 변경(관리자)

### 6. 공통(Common) 도메인

- **currentDateTime**  
  서버의 현재 날짜/시간 정보를 조회합니다.
- **sendDefaultSms**  
  자유양식 SMS(문자)를 발송합니다.
- **sendVerificationSms**  
  인증번호 SMS를 발송합니다.

## 기능별 역할 요약 표

| 도메인     | 주요 쿼리 예시                           | 주요 뮤테이션 예시                   | 기능 설명 |
|------------|------------------------------------------|--------------------------------------|-----------|
| Cart       | shoppingSession                          | addToCart, removeCartItem, updateCartItem, updateShoppingSession | 장바구니 품목 관리, 세션 관리 |
| Flower     | flowers                                  |                                      | 꽃 목록/검색 |
| Auction    | auctionResult, auctionResultDetail, auctionResultForSale, auctionResultForSaleDetail | updateAuctionResult                  | 경매 결과 조회, 경매 매물 관리 |
| Order      | orderSheets, orderSheet, orderItems, dailyOrderSheetAggregates, dailyOrderSheetAggregate, dailyOrderItems | orderCartItems, removeOrderSheet, updateOrderItemsPrice, upsertCustomOrderItems, removeCustomOrderItem, updateDailyOrderItemsPrice, issueOrderSheetReceipt, orderBatchCartItems | 주문서/주문 항목 관리, 집계, 영수증 발행 |
| User       | me, users, user                          | signUp, signIn, applyBizConnection, updateBizConnection, signOut, reissueToken, removeUser, upsertBusinessInfo, updatePassword | 유저 관리, 거래처 신청/승인, 인증 |
| Common     | currentDateTime                          | sendDefaultSms, sendVerificationSms  | 공통 유틸리티, SMS, 시간 정보 |

### 폴더 구조와 기능 연결 가이드

- 각 쿼리/뮤테이션은 adapter/in/graphql/datafetcher에서 요청을 받아, application 계층의 usecase/service/command를 통해 처리됩니다.
- 비즈니스 로직은 application/service, 도메인 모델은 domain/model 및 domain/vo에 위치합니다.
- 외부 시스템 연동(예: DB)은 adapter/out/persistence가 담당합니다.
- 공통 기능 및 유틸리티는 shared 디렉토리에 위치하여, 여러 도메인에서 재사용 가능합니다.

### 개발 및 협업 시 참고사항

- 새로운 기능 추가 시, 도메인 → 애플리케이션 → 어댑터 → 인프라 계층 순으로 책임을 분리하여 구현합니다.
- 폴더 네이밍 및 계층별 역할을 반드시 준수해주세요.
- OOP 안티패턴(예: God Object, Service/Manager 남용, 데이터 중심 설계 등)은 배제되어 있습니다.
- GraphQL Playground 등에서 실제 쿼리/뮤테이션 호출 예시를 확인할 수 있습니다.

이 구조는 대규모 서비스의 유지보수성과 확장성, 협업 효율을 극대화하기 위한 목적에 최적화되어 있습니다.  
각 계층과 폴더의 역할을 반드시 준수하며, 일관된 설계 원칙을 따라 개발해주시기 바랍니다.

---
MarketBill 개발팀 김태완(terry960302@gmail.com)
