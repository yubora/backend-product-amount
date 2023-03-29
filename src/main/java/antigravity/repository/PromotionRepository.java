package antigravity.repository;

import antigravity.domain.entity.Promotion;
import antigravity.domain.entity.PromotionType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PromotionRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Promotion getPromotion(int id) {
        String query = "SELECT * FROM `promotion` WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        return namedParameterJdbcTemplate.queryForObject(
                query,
                params,
                (rs, rowNum) -> Promotion.builder()
                        .id(rs.getInt("id"))
                        .promotion_type(PromotionType.valueOf(rs.getString("promotion_type")))
                        .discount_value(rs.getInt("discount_value"))
                        .build()
        );
    }
}
