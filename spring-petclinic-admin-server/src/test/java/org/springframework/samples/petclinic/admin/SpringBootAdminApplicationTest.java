package org.springframework.samples.petclinic.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ActiveProfiles("test")
class SpringBootAdminApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldLoadApplicationContext() {
        // Kiểm tra Spring Boot context có khởi động thành công không
        assertNotNull(applicationContext, "Application context should not be null");
    }

    @Test
    void shouldLoadSpringBootAdminApplication() {
        // Kiểm tra xem ứng dụng có được load trong context không
        SpringBootAdminApplication application = applicationContext.getBean(SpringBootAdminApplication.class);
        assertNotNull(application, "SpringBootAdminApplication should be loaded in context");
    }

    @Test
    void shouldRunMainMethodWithoutExceptions() {
        // Kiểm tra xem method main() có chạy mà không lỗi không
        assertDoesNotThrow(() -> SpringBootAdminApplication.main(new String[]{}));
    }
}
