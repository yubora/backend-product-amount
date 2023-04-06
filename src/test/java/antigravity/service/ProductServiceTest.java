package antigravity.service;

import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import antigravity.domain.entity.PromotionProducts;
import antigravity.domain.entity.PromotionType;
import antigravity.model.request.ProductInfoRequest;
import antigravity.model.response.ProductAmountResponse;
import antigravity.repository.ProductRepository;
import antigravity.repository.PromotionProductsRepository;
import antigravity.repository.PromotionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
class ProductServiceTest {

    @Autowired
    ProductService productService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    PromotionRepository promotionRepository;
    @Autowired
    PromotionProductsRepository promotionProductsRepository;

    @BeforeEach
    void clean() {
        productRepository.deleteAll();
        promotionRepository.deleteAll();
    }

    @Test
    @DisplayName("상품 쿠폰 적용 정상")
    void getProductAmountTest() throws ParseException {
        // given
        createFixture(125600, "2024-03-31");
        Product product = productRepository.findAll().get(0);
        List<PromotionProducts> promotionProducts = promotionProductsRepository.findValidByProductId(product.getId());
        long[] couponIds = promotionProducts.stream().mapToLong(pp -> pp.getPromotion().getId()).toArray();

        ProductInfoRequest request = ProductInfoRequest.builder()
                .productId(product.getId())
                .couponIds(couponIds)
                .build();

        // when
        ProductAmountResponse result = productService.getProductAmount(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOriginPrice()).isEqualTo(product.getPrice());
        assertThat(result.getDiscountPrice())
                .isEqualTo(productService.getDiscountPrice(product.getPrice(), promotionProducts));
        assertThat(result.getFinalPrice())
                .isEqualTo(cutOffPrice(result.getOriginPrice() - result.getDiscountPrice()));
    }

    @Test
    @DisplayName("적용 가능한 쿠폰이 없으면 할인 가격 0원")
    void getProductAmountTest2() throws ParseException {
        // given
        createFixture(125600, "2023-03-02"); // 기한 만료
        Product product = productRepository.findAll().get(0);
        List<PromotionProducts> promotionProducts = promotionProductsRepository.findValidByProductId(product.getId());
        long[] couponIds = promotionProducts.stream().mapToLong(pp -> pp.getPromotion().getId()).toArray();

        ProductInfoRequest request = ProductInfoRequest.builder()
                .productId(product.getId())
                .couponIds(couponIds)
                .build();

        // when
        ProductAmountResponse result = productService.getProductAmount(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOriginPrice()).isEqualTo(product.getPrice());
        assertThat(result.getDiscountPrice()).isEqualTo(0);
        assertThat(result.getFinalPrice())
                .isEqualTo(cutOffPrice(result.getOriginPrice()));
    }

    @Test
    @DisplayName("적용되는 총 할인 가격 조회 정상")
    void getDiscountPriceTest() throws ParseException {
        // given
        createFixture(150000, "2024-03-31");
        Product product = productRepository.findAll().get(0);
        List<PromotionProducts> promotions = promotionProductsRepository.findValidByProductId(product.getId());

        // when
        int result = productService.getDiscountPrice(product.getPrice(), promotions);

        //then
        assertThat(result).isEqualTo(20000);
    }

    @Test
    @DisplayName("상품가격 예외 처리 정상")
    void getPriceTest() throws ParseException {
        // given
        createFixture(5000, "2024-03-31");
        Product product = productRepository.findAll().get(0);
        List<PromotionProducts> promotionProducts = promotionProductsRepository.findValidByProductId(product.getId());
        long[] couponIds = promotionProducts.stream().mapToLong(pp -> pp.getPromotion().getId()).toArray();

        ProductInfoRequest request = ProductInfoRequest.builder()
                .productId(product.getId())
                .couponIds(couponIds)
                .build();

        // when & then
        String message = assertThrows(RuntimeException.class, () -> productService.getProductAmount(request)).getMessage();
        assertThat(message).isEqualTo("상품 가격은 10,000원 이상, 10,000,000원 이하만 가능합니다.");
    }

    private void createFixture(int originalPrice, String endDt) throws ParseException {
        System.out.println("#### FIXTURE ####");

        // 상품 세팅
        Product product = productRepository.save(Product.builder()
                .name("테스트 상품")
                .price(originalPrice)
                .build());

        // 쿠폰 세팅
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date use_started_at = formatter.parse("2023-03-01");
        Date use_ended_at = formatter.parse(endDt);

        List<Promotion> promotions = promotionRepository.saveAll(List.of(
                Promotion.builder()
                        .promotion_type(PromotionType.COUPON)
                        .name("5000원 할인쿠폰")
                        .discount_type("WON")
                        .discount_value(5000)
                        .use_started_at(use_started_at)
                        .use_ended_at(use_ended_at)
                        .build(),

                Promotion.builder()
                        .promotion_type(PromotionType.CODE)
                        .name("10% 할인코드")
                        .discount_type("PERCENT")
                        .discount_value(10)
                        .use_started_at(use_started_at)
                        .use_ended_at(use_ended_at)
                        .build()
        ));

        // 매핑 정보 세팅
        promotionProductsRepository.saveAll(List.of(
                PromotionProducts.builder()
                        .product(product)
                        .promotion(promotions.get(0))
                        .build(),

                PromotionProducts.builder()
                        .product(product)
                        .promotion(promotions.get(1))
                        .build()
        ));
    }

    private int cutOffPrice(int price) {
        // 천 단위 절사
        return (price / 1000) * 1000;
    }
}