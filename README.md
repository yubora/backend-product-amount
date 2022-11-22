# 안티그래비티 백엔드 기술과제

## [피팅노트 서비스에 "가격산정" 기능을 구현 해주세요!]

## 개요

- 이커머스 서비스는 판매촉진을 위해 다양한 상품가격 전략을 전개합니다.
- 정상가, 판매가로 이루어진 기본 골격에 더해 기간별 스팟할인, 쿠폰, 포인트 등 다양한 가격설정을 진행합니다.
- 우리는 이중에서 가장 흔하게 사용되는 _**쿠폰**_에 대해서 작성해보려 합니다.

## 요구사항

- 하나의 상품에 쿠폰과 할인코드를 적용하여 가격을 조회 합니다.
- 상품 가격 조회 api를 완성해주세요.

## 참고사항

- 상품과 프로모션 정보의 스키마와 데이터가 제공됩니다.
- promotion 테이블에는 `COUPON` 과 `CODE` 타입을 가지고있습니다.
    - `COUPON` = 금액할인,
    - `CODE` = %할인으로 계산합니다.
    - promotion 할인 금액을 계산 할때는 상품 기존 가격에 대해 계산 해주시면 됩니다.
- promotion_products 테이블에는 각 promotion에 적용 될 상품들의 매핑정보 입니다.

## 제약사항

- controller request 객체에 parameter는 그대로 사용 해주세요.
    - 상품 1의 프로모션 2가지 적용
- 결과는 ProductAmountResponse 객체를 리턴 해주세요. _주석참고_
- 쿠폰이 적용되는지 검증 로직이 있어야 합니다.
- 촘촘한 검증 코드가 요구됩니다.

## 도메인 로직

- 최소 상품가격은 ₩ 10,000 입니다.
- 최대 상품가격은 ₩ 10,000,000 입니다.
- 최종 상품 금액은 천단위 절삭합니다.

## 기타

- 자유롭게 라이브러리를 추가 하거나 소스 코드를 수정할 수 있습니다.
- 중복 코드 최소화를 고려해주세요.