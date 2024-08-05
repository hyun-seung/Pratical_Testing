package cafeKiosk.spring.domain.orderproduct;

import cafeKiosk.spring.domain.BaseEntity;
import cafeKiosk.spring.domain.order.Order;
import cafeKiosk.spring.domain.product.Product;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class OrderProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Builder
    public OrderProduct(Order order, Product product) {
        this.order = order;
        this.product = product;
    }
}
