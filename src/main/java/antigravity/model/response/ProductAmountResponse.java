package antigravity.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductAmountResponse {
    private String name; //상품명

    private int originPrice; //상품 기존 가격
    private int discountPrice; //총 할인 금액
    private int finalPrice; //확정 상품 가격

    public ProductAmountResponse(String name, int originPrice, int discountPrice, int finalPrice) {
        if (finalPrice < 10000 || finalPrice > 10000000) {
            throw new IllegalArgumentException("상품 가격은 10,000원 이상, 10,000,000원 이하만 가능합니다.");
        }

        this.name = name;
        this.originPrice = originPrice;
        this.discountPrice = discountPrice;
        this.finalPrice = finalPrice;
    }

    public int getFinalPrice() {
        // 천 단위 절삭
        return (this.finalPrice / 1000) * 1000;
    }
}
