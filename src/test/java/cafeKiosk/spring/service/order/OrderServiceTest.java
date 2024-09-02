package cafeKiosk.spring.service.order;

import cafeKiosk.spring.controller.order.request.OrderCreateRequest;
import cafeKiosk.spring.domain.order.OrderRepository;
import cafeKiosk.spring.domain.orderproduct.OrderProductRepository;
import cafeKiosk.spring.domain.product.Product;
import cafeKiosk.spring.domain.product.ProductRepository;
import cafeKiosk.spring.domain.product.ProductSellingStatus;
import cafeKiosk.spring.domain.product.ProductType;
import cafeKiosk.spring.domain.stock.Stock;
import cafeKiosk.spring.domain.stock.StockRepository;
import cafeKiosk.spring.service.order.request.OrderCreateServiceRequest;
import cafeKiosk.spring.service.order.response.OrderResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
//@Transactional
//@DataJpaTest --> OrderService 못 찾음.
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private StockRepository stockRepository;

    @AfterEach
    void tearDown() {
        orderProductRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        stockRepository.deleteAllInBatch();
    }

    @DisplayName("주문번호 리스트를 받아 주문을 생성한다.")
    @Test
    void createOrder() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct("001", ProductType.HANDMADE, 1000);
        Product product2 = createProduct("002", ProductType.HANDMADE, 3000);
        Product product3 = createProduct("003", ProductType.HANDMADE, 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        OrderCreateServiceRequest orderCreateServiceRequest = OrderCreateServiceRequest.builder().productNumbers(List.of("001", "002")).build();

        // when
        OrderResponse orderResponse = orderService.createOrder(orderCreateServiceRequest, registeredDateTime);

        // then
        assertThat(orderResponse.getId()).isNotNull();
        assertThat(orderResponse).extracting("registeredDateTime", "totalPrice").contains(registeredDateTime, 4000);
        assertThat(orderResponse.getProducts()).hasSize(2).extracting("productNumber", "price").containsExactlyInAnyOrder(tuple("001", 1000), tuple("002", 3000));
    }

    @DisplayName("중복되는 상품번호 리스트로 주문을 생성할 수 있다.")
    @Test
    void createOrderWithDuplicateProductNumbers() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct("001", ProductType.HANDMADE, 1000);
        Product product2 = createProduct("002", ProductType.HANDMADE, 3000);
        Product product3 = createProduct("003", ProductType.HANDMADE, 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        OrderCreateServiceRequest orderCreateServiceRequest = OrderCreateServiceRequest.builder().productNumbers(List.of("001", "001")).build();

        // when
        OrderResponse orderResponse = orderService.createOrder(orderCreateServiceRequest, registeredDateTime);

        // then
        assertThat(orderResponse.getId()).isNotNull();
        assertThat(orderResponse).extracting("registeredDateTime", "totalPrice").contains(registeredDateTime, 2000);
        assertThat(orderResponse.getProducts()).hasSize(2).extracting("productNumber", "price").containsExactlyInAnyOrder(tuple("001", 1000), tuple("001", 1000));
    }

    @DisplayName("재고와 관련된 상품이 포함되어 있는 주문번호 리스트를 받아 주문을 생성한다.")
    @Test
    void createOrderWithStock() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct("001", ProductType.BOTTLE, 1000);
        Product product2 = createProduct("002", ProductType.BAKERY, 3000);
        Product product3 = createProduct("003", ProductType.HANDMADE, 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        Stock stock1 = Stock.create("001", 2);
        Stock stock2 = Stock.create("002", 2);
        stockRepository.saveAll(List.of(stock1, stock2));

        OrderCreateServiceRequest orderCreateServiceRequest = OrderCreateServiceRequest.builder()
                .productNumbers(List.of("001", "001", "002", "003"))
                .build();

        // when
        OrderResponse orderResponse = orderService.createOrder(orderCreateServiceRequest, registeredDateTime);

        // then
        assertThat(orderResponse.getId()).isNotNull();
        assertThat(orderResponse).extracting("registeredDateTime", "totalPrice").contains(registeredDateTime, 10000);
        assertThat(orderResponse.getProducts()).hasSize(4)
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(
                        tuple("001", 1000),
                        tuple("001", 1000),
                        tuple("002", 3000),
                        tuple("003", 5000)
                );

        List<Stock> stocks = stockRepository.findAll();
        assertThat(stocks).hasSize(2)
                .extracting("productNumber", "quantity")
                .containsExactlyInAnyOrder(
                        tuple("001", 0),
                        tuple("002", 1)
                );
    }

    @DisplayName("재고가 부족한 상품으로 주문을 생성하려는 경우 예외가 발생한다.")
    @Test
    void createOrderWithNoStock() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct("001", ProductType.BOTTLE, 1000);
        Product product2 = createProduct("002", ProductType.BAKERY, 3000);
        Product product3 = createProduct("003", ProductType.HANDMADE, 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        Stock stock1 = Stock.create("001", 1);
        Stock stock2 = Stock.create("002", 1);
        stockRepository.saveAll(List.of(stock1, stock2));

        OrderCreateServiceRequest orderCreateServiceRequest = OrderCreateServiceRequest.builder()
                .productNumbers(List.of("001", "001", "002", "003"))
                .build();

        // when //then
        assertThatThrownBy(() -> orderService.createOrder(orderCreateServiceRequest, registeredDateTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 부족한 상품이 있습니다.");
    }

    private Product createProduct(String productNumber, ProductType type, int price) {
        return Product.builder().productNumber(productNumber).type(type).price(price).sellingStatus(ProductSellingStatus.SELLING).name("메뉴 이름").build();
    }
}