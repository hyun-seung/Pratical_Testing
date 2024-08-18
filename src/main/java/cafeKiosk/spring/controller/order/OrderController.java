package cafeKiosk.spring.controller.order;

import cafeKiosk.spring.api.ApiResponse;
import cafeKiosk.spring.controller.order.request.OrderCreateRequest;
import cafeKiosk.spring.service.order.OrderService;
import cafeKiosk.spring.service.order.request.OrderCreateServiceRequest;
import cafeKiosk.spring.service.order.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/v1/orders/new")
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        LocalDateTime registeredDateTime = LocalDateTime.now();
        OrderCreateServiceRequest serviceRequest = request.toServiceRequest();
        return ApiResponse.ok(orderService.createOrder(serviceRequest, registeredDateTime));
    }
}
