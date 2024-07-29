package cafeKiosk.spring.service.product;

import cafeKiosk.spring.domain.Product;
import cafeKiosk.spring.domain.ProductRepository;
import cafeKiosk.spring.domain.ProductSellingStatus;
import cafeKiosk.spring.service.product.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponse> getSellingProducts() {
        List<Product> products = productRepository.findAllBySellingStatusIn(ProductSellingStatus.forDisplay());

        return products.stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }
}
