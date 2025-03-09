package org.springframework.samples.petclinic.genai;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ActiveProfiles("test")
class GenAIServiceApplicationTest {

    @Test
    void contextLoads() {
        // Kiểm tra xem Spring Boot context có khởi động thành công không
        assertDoesNotThrow(() -> GenAIServiceApplication.main(new String[]{}));
    }
}
