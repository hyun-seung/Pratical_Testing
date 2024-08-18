package cafeKiosk.spring.controller.product;

import cafeKiosk.spring.api.ApiResponse;
import cafeKiosk.spring.controller.product.request.ProductCreateRequest;
import cafeKiosk.spring.service.product.ProductService;
import cafeKiosk.spring.service.product.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ProductController {

    private final ProductService productService;

    @PostMapping("/api/v1/products/new")
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        return ApiResponse.ok(productService.createProduct(request.toServiceRequest()));
    }

    @GetMapping("/api/v1/products/selling")
    public ApiResponse<List<ProductResponse>> getSelingProducts() {
        return ApiResponse.ok(productService.getSellingProducts());
    }
}
