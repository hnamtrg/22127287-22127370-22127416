/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootTest
class PetclinicConfigServerApplicationTests {

	@Test
	void contextLoads() {
	}

    @Test
    void applicationStartsSuccessfully() {
        // Kiểm tra xem application có khởi động mà không gặp lỗi không
        ApplicationContext context = SpringApplication.run(ConfigServerApplication.class);
        assertThat(context).isNotNull();
        SpringApplication.exit(context);
    }

	 @Test
    void testConfigServerAnnotationExists() {
        // Kiểm tra xem @EnableConfigServer có thực sự được áp dụng không
        boolean isAnnotationPresent = ConfigServerApplication.class.isAnnotationPresent(EnableConfigServer.class);
        assertThat(isAnnotationPresent).isTrue();
    }

    @Test
    void testApplicationContextContainsBeans() {
        // Tạo một application context
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConfigServerApplication.class)) {
            assertThat(context.containsBean("configServerApplication")).isFalse(); // Kiểm tra bean mặc định
            assertThat(context.getBeanDefinitionCount()).isGreaterThan(0); // Kiểm tra số lượng bean
        }
    }

    @Test
    void testMainMethod() {
        // Kiểm tra xem main() có gọi SpringApplication.run hay không
        SpringApplication mockSpringApplication = mock(SpringApplication.class);
        String[] args = {};
        ConfigServerApplication.main(args);
        verify(mockSpringApplication).run(ConfigServerApplication.class, args);
    }
}
