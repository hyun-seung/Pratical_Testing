package cafeKiosk.spring.controller.product;

import cafeKiosk.spring.service.product.ProductService;
import cafeKiosk.spring.service.product.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ProductController {

    private final ProductService productService;

    @GetMapping("/api/v1/products/selling")
    public List<ProductResponse> getSelingProducts() {
        return productService.getSellingProducts();
    }
}
