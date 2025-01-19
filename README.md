# MarketBill Core Server

MarketBill은 화훼 시장의 경매와 거래를 디지털화하는 플랫폼입니다. 이 프로젝트는 MarketBill의 코어 백엔드 서버를 구현합니다.

## 기술 스택

- **언어**: Kotlin
- **프레임워크**: Spring Boot
- **API**: GraphQL (Netflix DGS Framework)
- **보안**: Spring Security, JWT
- **데이터베이스**: JPA/Hibernate
- **외부 서비스 연동**: 
  - SMS 메시징 서비스
  - 파일 처리 서비스

## 주요 기능

### 1. 사용자 관리
- 사용자 유형별 계정 관리 (소매상, 도매상 사장, 도매상 직원)
- JWT 기반 인증
- 사용자 간 비즈니스 연결(거래처) 관리
- 비밀번호 관리 및 업데이트

### 2. 경매 시스템
- 실시간 경매 결과 조회
- 낙찰 정보 관리
- 판매용 경매 결과 관리
- 경매 상품 가격 업데이트

### 3. 주문 시스템
- 장바구니 기능
- 주문서 생성 및 관리
- 주문 아이템 관리
- 일일 주문 집계
- 주문서 영수증 발행
- 배치 주문 처리

### 4. 상품 관리
- 꽃 종류 및 정보 관리
- 꽃 이미지 관리
- 상품 등급 관리

## 보안

- Spring Security를 사용한 역할 기반 접근 제어 (RBAC)
- JWT 토큰 기반 인증
- 패스워드 암호화

## 데이터 로딩 최적화

- DataLoader 패턴을 사용하여 N+1 문제 해결
- JPA Specification을 활용한 동적 쿼리 구현
- 페이지네이션 지원

MarketBill 개발팀 (terry960302@gmail.com)
