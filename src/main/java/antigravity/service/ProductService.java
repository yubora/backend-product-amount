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
        int discountPrice = getDiscountPrice(product.getPrice(), promotionProducts);

        return ProductAmountResponse.builder()
                .name(product.getName())
                .originPrice(product.getPrice())
                .discountPrice(discountPrice)
                .finalPrice(product.getPrice() - discountPrice)
                .build();
    }

    public int getDiscountPrice(int originalPrice, List<PromotionProducts> promotionProducts) {
        int totalDiscountPrice = 0;

        // FIXME 종료임박순으로 조회하여 순서대로 적용
        for (PromotionProducts pp : promotionProducts) {
            Promotion promotion = pp.getPromotion();
            int promotionPrice = promotion.getDiscount_value(originalPrice);

            if (MIN_PRICE <= originalPrice - (promotionPrice + totalDiscountPrice)) {
                totalDiscountPrice += promotionPrice;
            }
        }
        return totalDiscountPrice;
    }
}
