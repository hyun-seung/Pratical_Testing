package cafeKiosk.unit.beverage;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AmericanoTest {

    @Test
    void getName() {
        Americano americano = new Americano();

        // Junit
        assertEquals(americano.getName(), "아메리카노");
        // AssertJ
        assertThat(americano.getName()).isEqualTo("아메리카노");
    }
}