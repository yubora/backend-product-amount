package antigravity.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private PromotionType promotion_type; //쿠폰 타입 (쿠폰, 코드)
    private String name;
    private String discount_type; // WON : 금액 할인, PERCENT : %할인
    private int discount_value; // 할인 금액 or 할인 %
    private Date use_started_at; // 쿠폰 사용가능 시작 기간
    private Date use_ended_at; // 쿠폰 사용가능 종료 기간

    @Builder
    public Promotion(PromotionType promotion_type, String name, String discount_type, int discount_value, Date use_started_at, Date use_ended_at) {
        this.promotion_type = promotion_type;
        this.name = name;
        this.discount_type = discount_type;
        this.discount_value = discount_value;
        this.use_started_at = use_started_at;
        this.use_ended_at = use_ended_at;
    }
}
