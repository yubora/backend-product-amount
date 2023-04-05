package antigravity.service;

import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import antigravity.domain.entity.PromotionProducts;
import antigravity.domain.entity.PromotionType;
import antigravity.model.request.ProductInfoRequest;
import antigravity.model.response.ProductAmountResponse;
import antigravity.repository.ProductRepository;
import antigravity.repository.PromotionProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final static int MIN_PRICE = 10000;
    private final ProductRepository productRepository;
    private final PromotionProductsRepository promotionProductsRepository;

    @Transactional(readOnly = true)
    public ProductAmountResponse getProductAmount(ProductInfoRequest request) {
        if (request == null || request.getProductId() == null) {
            throw new IllegalArgumentException();
        }

        Product product = productRepository.findById(request.getProductId()).orElseThrow();
        List<PromotionProducts> promotionProducts = promotionProductsRepository.findValidByProductId(request.getProductId());

        // 총 할인 가격 조회
        int discountPrice = 0;
        for (PromotionProducts pp : promotionProducts) {
            Promotion promotion = pp.getPromotion();
            discountPrice += getDiscountPrice(product, promotion);

            // FIXME 프로모션 적용 후, 최소 가격 이하가 될 경우 프로모션 우선순위는?
            if (product.getPrice() - discountPrice <= MIN_PRICE) {
                discountPrice -= getDiscountPrice(product, promotion);
                break;
            }
        }


        return ProductAmountResponse.builder()
                .name(product.getName())
                .originPrice(product.getPrice())
                .discountPrice(discountPrice)
                .finalPrice(product.getPrice() - discountPrice)
                .build();
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
