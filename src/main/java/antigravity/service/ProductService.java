package antigravity.service;

import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import antigravity.domain.entity.PromotionType;
import antigravity.model.request.ProductInfoRequest;
import antigravity.model.response.ProductAmountResponse;
import antigravity.repository.ProductRepository;
import antigravity.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;

    public ProductAmountResponse getProductAmount(ProductInfoRequest request) {
        System.out.println("상품 가격 추출 로직을 완성 시켜주세요.");

        Product product = productRepository.getProduct(request.getProductId());

        // 총 할인 가격 조회
        int discountPrice = 0;
        for (int couponId : request.getCouponIds()) {
            Promotion promotion = getPromotion(couponId);
            discountPrice += getDiscountPrice(product, promotion);
        }

        return ProductAmountResponse.builder()
                .name(product.getName())
                .originPrice(product.getPrice())
                .discountPrice(discountPrice)
                .finalPrice(product.getPrice() - discountPrice)
                .build();
    }

    public Promotion getPromotion(int id) {
        return promotionRepository.getPromotion(id);
    }

    /**
     * 프로모션 타입별 최종 할인 금액 조회
     * PromotionType.COUPON: discount_value 반환
     * PromotionType.CODE: 상품 정상가의 discount_value(%) 가격 환산하여 반환
     */
    public int getDiscountPrice(Product product, Promotion promotion) {
        PromotionType promotionType = promotion.getPromotion_type();
        if (promotionType == PromotionType.CODE) {
            double percent = promotion.getDiscount_value() / 100.00;
            return (int) (product.getPrice() * percent);
        }
        return promotion.getDiscount_value();
    }
}
