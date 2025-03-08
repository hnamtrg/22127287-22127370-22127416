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
package org.springframework.samples.petclinic.vets.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.vets.VetsServiceApplication;
import org.springframework.samples.petclinic.vets.model.Specialty;
import org.springframework.samples.petclinic.vets.model.Vet;
import org.springframework.samples.petclinic.vets.model.VetRepository;
import org.springframework.samples.petclinic.vets.system.VetsProperties;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Maciej Szarlinski
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(VetResource.class)
@ActiveProfiles("test")
class VetResourceTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    VetRepository vetRepository;

    @MockBean
    Vet vet;

    @BeforeEach
    public void setUp() {
        vet = new Vet(); // Khởi tạo một instance mới của Vet trước mỗi test
    }

    @Test
    void shouldGetAListOfVets() throws Exception {

        Vet vet = new Vet();
        vet.setId(1);

        given(vetRepository.findAll()).willReturn(asList(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));
    }
    @Test
    void testInstanceCreation() {
        // Kiểm tra xem instance của Specialty có được tạo thành công hay không
        Specialty specialty = new Specialty();
        assertNotNull(specialty, "Instance của Specialty không được null");
    }

    @Test
    void testGetIdInitiallyNull() {
        // Kiểm tra rằng id ban đầu là null vì nó chưa được gán giá trị
        Specialty specialty = new Specialty();
        assertNull(specialty.getId(), "Id ban đầu phải là null vì chưa được sinh tự động");
    }

    @Test
    void testSetAndGetName() {
        // Kiểm tra setter và getter của name
        Specialty specialty = new Specialty();
        String expectedName = "Dentistry";
        specialty.setName(expectedName);
        assertEquals(expectedName, specialty.getName(), "Giá trị của name phải khớp với giá trị đã thiết lập");
    }

    @Test
    void testGetNameInitiallyNull() {
        // Kiểm tra rằng name ban đầu là null khi chưa thiết lập
        Specialty specialty = new Specialty();
        assertNull(specialty.getName(), "Name ban đầu phải là null vì chưa được thiết lập");
    }

    @Test
    void testAddSpecialty() {
        // Tạo một đối tượng Specialty
        Specialty specialty = new Specialty();
        specialty.setName("Surgery");

        // Gọi phương thức addSpecialty
        vet.addSpecialty(specialty);

        // Kiểm tra xem specialty đã được thêm vào tập hợp chưa
        assertTrue(vet.getSpecialties().contains(specialty), "Specialty phải được thêm vào tập hợp");
        assertEquals(1, vet.getSpecialties().size(), "Kích thước tập hợp phải là 1 sau khi thêm");
    }

    @Test
    void testSetFirstName() {
        // Thiết lập giá trị cho firstName
        String firstName = "Anna";
        vet.setFirstName(firstName);

        // Kiểm tra xem firstName đã được thiết lập đúng chưa
        assertEquals(firstName, vet.getFirstName(), "Giá trị firstName phải khớp với giá trị đã thiết lập");
    }

    @Test
    void testSetLastName() {
        // Thiết lập giá trị cho lastName
        String lastName = "Smith";
        vet.setLastName(lastName);

        // Kiểm tra xem lastName đã được thiết lập đúng chưa
        assertEquals(lastName, vet.getLastName(), "Giá trị lastName phải khớp với giá trị đã thiết lập");
    }

    @Test
    void testMainMethodRunsSuccessfully() {
        // Kiểm tra xem phương thức main có thể chạy mà không gặp lỗi không
        VetsServiceApplication.main(new String[]{});
        // Nếu không có ngoại lệ nào được ném ra, test thành công
    }

    @Test
    void testApplicationAnnotations() {
        // Kiểm tra xem lớp có các annotation cần thiết không
        assertTrue(VetsServiceApplication.class.isAnnotationPresent(EnableDiscoveryClient.class),
                "Lớp phải có annotation @EnableDiscoveryClient");
        assertTrue(VetsServiceApplication.class.isAnnotationPresent(SpringBootApplication.class),
                "Lớp phải có annotation @SpringBootApplication");
        assertTrue(VetsServiceApplication.class.isAnnotationPresent(EnableConfigurationProperties.class),
                "Lớp phải có annotation @EnableConfigurationProperties");
    }

    @Test
    void testConfigurationProperties() {
        // Kiểm tra xem @EnableConfigurationProperties có được cấu hình đúng với VetsProperties không
        EnableConfigurationProperties annotation = VetsServiceApplication.class.getAnnotation(EnableConfigurationProperties.class);
        assertTrue(annotation.value()[0].equals(VetsProperties.class),
                "Annotation @EnableConfigurationProperties phải cấu hình với VetsProperties");
    }
}
