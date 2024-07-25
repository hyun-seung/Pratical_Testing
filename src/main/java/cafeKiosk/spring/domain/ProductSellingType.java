package cafeKiosk.spring.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductSellingType {

    SELLING("판매 중"),
    HOLD("판매 보류"),
    STOP_SELLING("판매 중지");

    private final String text;
}
