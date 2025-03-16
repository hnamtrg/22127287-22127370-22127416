package org.springframework.samples.petclinic.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class ConfigServerApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        // Kiểm tra context được load thành công
        assertThat(context).isNotNull();
    }

    @Test
    void testMainMethod() {
        // Kiểm tra xem ứng dụng có thể khởi động mà không lỗi
        ConfigServerApplication.main(new String[]{});
    }
}
