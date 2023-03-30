package antigravity.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int price;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<PromotionProducts> promotionProducts = new ArrayList<>();

    @Builder
    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }
}
