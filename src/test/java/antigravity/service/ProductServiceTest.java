package antigravity.service;

import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import antigravity.domain.entity.PromotionType;
import antigravity.model.request.ProductInfoRequest;
import antigravity.model.response.ProductAmountResponse;
import antigravity.repository.ProductRepository;
import antigravity.repository.PromotionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class ProductServiceTest {

    @Autowired
    ProductService productService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    PromotionRepository promotionRepository;

    @Test
    @DisplayName("상품 쿠폰 적용 정상")
    void getProductAmountTest() {
        // given
        int[] couponIds = {1, 2};
        ProductInfoRequest request = ProductInfoRequest.builder()
                .productId(1)
                .couponIds(couponIds)
                .build();

        Product product = productRepository.getProduct(request.getProductId());

        List<Promotion> promotions = new ArrayList<>();
        for (int id : couponIds) {
            promotions.add(promotionRepository.getPromotion(id));
        }

        // when
        ProductAmountResponse result = productService.getProductAmount(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOriginPrice()).isEqualTo(product.getPrice());
        assertThat(result.getDiscountPrice())
                .isEqualTo(promotions.stream()
                        .mapToInt(promotion -> productService.getDiscountPrice(product, promotion))
                        .sum());
    }

    @Test
    @DisplayName("promotion 조회 정상")
    void getPromotionTest() {
        // given & when
        Promotion result = productService.getPromotion(1);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getPromotion_type()).isEqualTo(PromotionType.COUPON);
        assertThat(result.getDiscount_value()).isEqualTo(30000);
    }

    @Test
    @DisplayName("할인 가격 조회 정상")
    void getDiscountPriceTest() {
        // given
        int[] couponIds = {1, 2};
        ProductInfoRequest request = ProductInfoRequest.builder()
                .productId(1)
                .couponIds(couponIds)
                .build();

        Product product = productRepository.getProduct(request.getProductId());
        List<Promotion> promotions = new ArrayList<>();
        for (int id : couponIds) {
            promotions.add(promotionRepository.getPromotion(id));
        }

        for (Promotion promotion : promotions) {
            // when
            int result = productService.getDiscountPrice(product, promotion);

            //then
            assertPrice(promotion, product, result);
        }
    }

    private void assertPrice(Promotion promotion, Product product, double expectedPrice) {
        PromotionType promotionType = promotion.getPromotion_type();

        if (promotionType.equals(PromotionType.CODE)) {
            assertThat(expectedPrice).isEqualTo(product.getPrice() * promotion.getDiscount_value() / 100.00);
        } else {
            assertThat(expectedPrice).isEqualTo(promotion.getDiscount_value());
        }
    }
}