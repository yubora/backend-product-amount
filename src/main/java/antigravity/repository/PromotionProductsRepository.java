package antigravity.repository;

import antigravity.domain.entity.PromotionProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PromotionProductsRepository extends JpaRepository<PromotionProducts, Long> {
    @Query("SELECT pp FROM PromotionProducts pp JOIN pp.promotion WHERE pp.product.id = :productId AND NOW() BETWEEN pp.promotion.use_started_at AND pp.promotion.use_ended_at")
    List<PromotionProducts> findValidByProductId(Long productId);
}
