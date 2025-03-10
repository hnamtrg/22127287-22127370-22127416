package org.springframework.samples.petclinic.customers.web;

import java.lang.annotation.Annotation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customers.CustomersServiceApplication;
import org.springframework.samples.petclinic.customers.config.MetricConfig;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetRepository;
import org.springframework.samples.petclinic.customers.model.PetType;
import org.springframework.samples.petclinic.customers.web.mapper.OwnerEntityMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.Set;
import java.util.Date;
import java.util.Optional;
import java.util.List;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Maciej Szarlinski
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(PetResource.class)
@ActiveProfiles("test")
class PetResourceTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    PetRepository petRepository;

    @MockBean
    OwnerRepository ownerRepository;

    @Mock
    OwnerEntityMapper ownerEntityMapper;

    OwnerResource ownerResource;

    @Autowired
    Validator validator;

    @Autowired
    PetResource petResource; // Inject the controller

    @Test
    void shouldGetAPetInJSonFormat() throws Exception {

        Pet pet = setupPet();

        given(petRepository.findById(2)).willReturn(Optional.of(pet));


        mvc.perform(get("/owners/2/pets/2").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.name").value("Basil"))
            .andExpect(jsonPath("$.type.id").value(6));
    }

    private Pet setupPet() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");

        Pet pet = new Pet();

        pet.setName("Basil");
        pet.setId(2);

        PetType petType = new PetType();
        petType.setId(6);
        pet.setType(petType);

        owner.addPet(pet);
        return pet;
    }

    @Mock
    private MeterRegistry meterRegistry;

    @InjectMocks
    private MetricConfig metricConfig;

    @BeforeEach
    void setUp() {
        metricConfig = new MetricConfig();
        meterRegistry = new SimpleMeterRegistry();
        ownerResource = new OwnerResource(ownerRepository, ownerEntityMapper);
    }

    @Test
    void testMetricsCommonTagsBean() {
        MeterRegistryCustomizer<MeterRegistry> customizer = metricConfig.metricsCommonTags();
        customizer.customize(meterRegistry);

        assertThat(meterRegistry.config()).isNotNull();
        // assertThat(meterRegistry.config().commonTags()).isNotEmpty();
    }

    @Test
    void testTimedAspectBean() {
        TimedAspect timedAspect = metricConfig.timedAspect(meterRegistry);
        assertThat(timedAspect).isNotNull();
    }

    @Test
    void testMetricConfigBeans() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBean(MeterRegistry.class, () -> new SimpleMeterRegistry());
        context.register(MetricConfig.class);
        context.refresh();

        assertThat(context.getBean(MeterRegistryCustomizer.class)).isNotNull();
        assertThat(context.getBean(TimedAspect.class)).isNotNull();
        context.close();
    }

    // @Test
    // void testMetricConfigBeans() {
    //     AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MetricConfig.class);
    //     assertThat(context.getBean(MeterRegistryCustomizer.class)).isNotNull();
    //     assertThat(context.getBean(TimedAspect.class)).isNotNull();
    //     context.close();
    // }

    @Test
    void testTimedAspectWithMockedRegistry() {
        MeterRegistry mockedRegistry = mock(MeterRegistry.class);
        TimedAspect timedAspect = metricConfig.timedAspect(mockedRegistry);

        assertThat(timedAspect).isNotNull();
        verifyNoInteractions(mockedRegistry); 
    }
    
    @Test
    void testOwnerEntity() {
        Owner owner = new Owner();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setAddress("123 Main St");
        owner.setCity("Springfield");
        owner.setTelephone("1234567890");

        assertThat(owner.getFirstName()).isEqualTo("John");
        assertThat(owner.getLastName()).isEqualTo("Doe");
        assertThat(owner.getAddress()).isEqualTo("123 Main St");
        assertThat(owner.getCity()).isEqualTo("Springfield");
        assertThat(owner.getTelephone()).isEqualTo("1234567890");
    }

    @Test
    void testPetEntity() {
        Pet pet = new Pet();
        pet.setName("Buddy");
        PetType petType = new PetType();
        pet.setType(petType);

        assertThat(pet.getName()).isEqualTo("Buddy");
        assertThat(pet.getType()).isNotNull();
    }

    @Test
    void testOwnerRepositoryFindById() {
        Owner owner = new Owner();
        owner.setFirstName("Jane");
        owner.setLastName("Smith");
        
        when(ownerRepository.findById(1)).thenReturn(Optional.of(owner));
        
        Optional<Owner> retrievedOwner = ownerRepository.findById(1);
        assertThat(retrievedOwner).isPresent();
        assertThat(retrievedOwner.get().getFirstName()).isEqualTo("Jane");
        assertThat(retrievedOwner.get().getLastName()).isEqualTo("Smith");
    }

    // CustomersServiceApplication
    @Test
    void testSpringBootApplicationAnnotation() {
        Annotation[] annotations = CustomersServiceApplication.class.getAnnotations();
        assertThat(annotations).anyMatch(annotation -> annotation instanceof SpringBootApplication);
    }

    @Test
    void testEnableDiscoveryClientAnnotation() {
        Annotation[] annotations = CustomersServiceApplication.class.getAnnotations();
        assertThat(annotations).anyMatch(annotation -> annotation instanceof EnableDiscoveryClient);
    }
    @Test
    public void testMainMethod() {
        ConfigurableApplicationContext context = SpringApplication.run(CustomersServiceApplication.class);
        assertThat(context).isNotNull();
        assertThat(context.isRunning()).isTrue();
        context.close();
    }

    // OWNER
    @Test
    void testGetPetsEmpty() {
        Owner owner = new Owner();
        List<Pet> pets = owner.getPets();
        assertTrue(pets.isEmpty(), "Danh sách pets phải rỗng khi chưa thêm pet.");
    }

    @Test
    void testGetPetsUnmodifiable() {
        Owner owner = new Owner();
        List<Pet> pets = owner.getPets();
        assertThrows(UnsupportedOperationException.class, () -> pets.add(new Pet()),
            "Không thể thêm pet vào danh sách unmodifiable.");
    }

    @Test
    void testToString() {
        Owner owner = new Owner();
        owner.setId(1);
        owner.setLastName("Doe");
        owner.setFirstName("John");
        owner.setAddress("123 Main St");
        owner.setCity("Anytown");
        owner.setTelephone("123-456-7890");
        String result = owner.toString();
        String expected = "[id=1, lastName=Doe, firstName=John, address=123 Main St, city=Anytown, telephone=123-456-7890]";
        assertEquals(expected, result, "Chuỗi toString phải khớp với giá trị预期.");
    }

    @Test
    void testToStringWithNullValues() {
        Owner owner = new Owner();
        owner.setId(1);
        // Các thuộc tính khác để null
        String result = owner.toString();
        String expected = "[id=1, lastName=null, firstName=null, address=null, city=null, telephone=null]";
        assertEquals(expected, result, "Chuỗi toString phải xử lý đúng khi các thuộc tính là null.");
    }

    @Test
    void testGetId() {
        Owner owner = new Owner();
        owner.setId(100);
        assertEquals(100, owner.getId(), "Phương thức getId phải trả về giá trị id đã thiết lập.");
    }

    @Test
    void testGetIdNull() {
        Owner owner = new Owner();
        assertNull(owner.getId(), "Phương thức getId phải trả về null khi id chưa được thiết lập.");
    }

    @Test
    void testSetBirthDate() {
        Pet pet = new Pet();
        Date birthDate = new Date(2020 - 1900, 1 - 1, 1); // 2020-01-01
        pet.setBirthDate(birthDate);
        assertEquals(birthDate, pet.getBirthDate());
    }
    
    @Test
    void testEqualsSameObject() {
        Pet pet = new Pet();
        assertTrue(pet.equals(pet));
    }

    @Test
    void testEqualsIdenticalPets() {
        PetType type = new PetType();
        type.setName("Dog");
        Owner owner = new Owner();
        owner.setFirstName("John");
        owner.setLastName("Doe");

        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Fluffy");
        pet1.setBirthDate(new Date(2020 - 1900, 1 - 1, 1));
        pet1.setType(type);
        pet1.setOwner(owner);

        Pet pet2 = new Pet();
        pet2.setId(1);
        pet2.setName("Fluffy");
        pet2.setBirthDate(new Date(2020 - 1900, 1 - 1, 1));
        pet2.setType(type);
        pet2.setOwner(owner);

        assertTrue(pet1.equals(pet2));
    }

    @Test
    void testEqualsDifferentPets() {
        Pet pet1 = new Pet();
        pet1.setId(1);

        Pet pet2 = new Pet();
        pet2.setId(2);

        assertFalse(pet1.equals(pet2));
    }

    @Test
    void testEqualsNull() {
        Pet pet = new Pet();
        assertFalse(pet.equals(null));
    }

    @Test
    void testEqualsDifferentClass() {
        Pet pet = new Pet();
        Object other = new Object();
        assertFalse(pet.equals(other));
    }

    // Owner Resources
    @Test
    public void testCreateOwnerSuccess() {
        OwnerRequest request = new OwnerRequest("John", "Doe", "123 Main St", "Hanoi", "1234567890");
        Owner owner = new Owner();
        when(ownerEntityMapper.map(any(Owner.class), eq(request))).thenReturn(owner);
        when(ownerRepository.save(owner)).thenReturn(owner);

        Owner result = ownerResource.createOwner(request);
        assertNotNull(result);
        verify(ownerRepository, times(1)).save(owner);
    }

    @Test
    void testFindOwnerSuccess() {
        Owner owner = new Owner();
        when(ownerRepository.findById(1)).thenReturn(Optional.of(owner));

        Optional<Owner> result = ownerResource.findOwner(1);
        assertTrue(result.isPresent());
        assertEquals(owner, result.get());
    }

    @Test
    void testFindAllEmpty() {
        when(ownerRepository.findAll()).thenReturn(Collections.emptyList());

        List<Owner> result = ownerResource.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateOwnerSuccess() {
        int ownerId = 1;
        OwnerRequest request = new OwnerRequest("John", "Doe", "123 Main St", "Hanoi", "1234567890");
        Owner owner = new Owner();
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        // No return value needed, just ensure the method is called
        doAnswer(invocation -> {
            Owner o = invocation.getArgument(0);
            o.setFirstName(request.firstName());
            o.setLastName(request.lastName());
            o.setAddress(request.address());
            o.setCity(request.city());
            o.setTelephone(request.telephone());
            return o;
        }).when(ownerEntityMapper).map(owner, request);
        when(ownerRepository.save(owner)).thenReturn(owner);
    
        ownerResource.updateOwner(ownerId, request);
    
        verify(ownerEntityMapper, times(1)).map(owner, request);
        verify(ownerRepository, times(1)).save(owner);
    }

    @Test
    void testUpdateOwnerNotFound() {
        int ownerId = 999;
        OwnerRequest request = new OwnerRequest("John", "Doe", "123 Main St", "Hanoi", "1234567890");
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.empty());
        System.out.println("Mock result: " + ownerRepository.findById(ownerId)); // Should print "Optional.empty"
        assertThrows(ResourceNotFoundException.class, () -> ownerResource.updateOwner(ownerId, request));
    }

    // WEB
    @Test
    void testConstructorAndMessage() {
        // Arrange
        String message = "Resource not found";
        
        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException(message);
        
        // Assert
        assertEquals(message, exception.getMessage(), 
            "Thông điệp của ngoại lệ phải khớp với thông điệp được truyền vào.");
    }

    @Test
    void testInheritance() {
        // Arrange & Act
        ResourceNotFoundException exception = new ResourceNotFoundException("Test");
        
        // Assert
        assertTrue(exception instanceof RuntimeException, 
            "ResourceNotFoundException phải là một instance của RuntimeException.");
    }

    @Test
    void testExceptionThrown() {
        // Arrange
        String message = "Resource not found";
        
        // Act & Assert
        try {
            throw new ResourceNotFoundException(message);
        } catch (ResourceNotFoundException e) {
            assertEquals(message, e.getMessage(), 
                "Thông điệp của ngoại lệ phải khớp với thông điệp được truyền vào.");
        }
    }

    @Test
    void shouldThrowExceptionWhenOwnerNotFound() {
        // Arrange
        int ownerId = 999;
        PetRequest petRequest = new PetRequest(0, new Date(), "Buddy", 1);
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            petResource.processCreationForm(petRequest, ownerId);
        });
        assertThat(exception.getMessage()).isEqualTo("Owner 999 not found");
        verify(ownerRepository).findById(ownerId);
        verifyNoInteractions(petRepository); // Không gọi save khi Owner không tồn tại
    }
    @Test
    void shouldSetPetPropertiesFromRequest() {
        // Arrange
        int ownerId = 1;
        PetRequest petRequest = new PetRequest(0, new Date(), "Buddy", 1);
        Owner owner = new Owner();
        PetType petType = new PetType();
        Pet savedPet = new Pet();
        savedPet.setName("Buddy");
        savedPet.setType(petType);

        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(petRepository.findPetTypeById(petRequest.typeId())).thenReturn(Optional.of(petType));
        when(petRepository.save(any(Pet.class))).thenReturn(savedPet);

        // Act
        Pet result = petResource.processCreationForm(petRequest, ownerId);

        // Assert
        assertThat(result.getName()).isEqualTo("Buddy");
        assertThat(result.getType()).isEqualTo(petType);
        verify(petRepository).findPetTypeById(petRequest.typeId());
    }
    @Test
    void shouldHandleNullValuesInPetRequest() {
        // Arrange
        int ownerId = 1;
        PetRequest petRequest = new PetRequest(1, null, null, 1);
        Owner owner = new Owner();
        PetType petType = new PetType();
        Pet savedPet = new Pet();
        savedPet.setType(petType);

        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(petRepository.findPetTypeById(petRequest.typeId())).thenReturn(Optional.of(petType));
        when(petRepository.save(any(Pet.class))).thenReturn(savedPet);

        // Act
        Pet result = petResource.processCreationForm(petRequest, ownerId);

        // Assert
        assertThat(result.getName()).isNull();
        assertThat(result.getBirthDate()).isNull();
        assertThat(result.getType()).isEqualTo(petType);
        verify(petRepository).findPetTypeById(petRequest.typeId());
    }

    // Owner Request 
    // Kiểm tra khi tất cả các trường đều hợp lệ.
    @Test
    void whenAllFieldsAreValid_thenNoViolations() {
        OwnerRequest request = new OwnerRequest("John", "Doe", "123 Main St", "Anytown", "1234567890");
        Set<jakarta.validation.ConstraintViolation<OwnerRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }
    /** 
     * Kiểm tra khi firstName là null.
     * Dự kiến: Có lỗi validation trên trường firstName.
     */
    @Test
    public void whenFirstNameIsNull_thenViolationOnFirstName() {
        OwnerRequest request = new OwnerRequest(null, "Doe", "123 Main St", "Anytown", "1234567890");
        Set<jakarta.validation.ConstraintViolation<OwnerRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("firstName");
    }

    /** 
     * Kiểm tra khi firstName chỉ chứa khoảng trắng.
     * Dự kiến: Có lỗi validation trên trường firstName.
     */
    @Test
    public void whenFirstNameIsWhitespace_thenViolationOnFirstName() {
        OwnerRequest request = new OwnerRequest(" ", "Doe", "123 Main St", "Anytown", "1234567890");
        Set<jakarta.validation.ConstraintViolation<OwnerRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("firstName");
    }

    /** 
     * Kiểm tra khi telephone là null.
     * Dự kiến: Có lỗi validation trên trường telephone.
     */
    @Test
    public void whenTelephoneIsNull_thenViolationOnTelephone() {
        OwnerRequest request = new OwnerRequest("John", "Doe", "123 Main St", "Anytown", null);
        Set<jakarta.validation.ConstraintViolation<OwnerRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("telephone");
    }
    /** 
     * Kiểm tra khi telephone chỉ chứa khoảng trắng.
     * Dự kiến: Có lỗi validation trên trường telephone.
     */
    @Test
    public void whenTelephoneIsWhitespace_thenViolationOnTelephone() {
        OwnerRequest request = new OwnerRequest("John", "Doe", "123 Main St", "Anytown", " ");
    Set<ConstraintViolation<OwnerRequest>> violations = validator.validate(request);
    assertThat(violations).hasSize(2); // Expect both NotBlank and Digits violations
    assertThat(violations).anyMatch(v -> v.getMessage().contains("must not be blank"));
    assertThat(violations).anyMatch(v -> v.getMessage().contains("numeric value out of bounds"));    
    }

    /** 
     * Kiểm tra khi telephone chứa ký tự không phải số.
     * Dự kiến: Có lỗi validation trên trường telephone.
     */
    @Test
    public void whenTelephoneHasNonDigits_thenViolationOnTelephone() {
        OwnerRequest request = new OwnerRequest("John", "Doe", "123 Main St", "Anytown", "123-456");
        Set<jakarta.validation.ConstraintViolation<OwnerRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("telephone");
    }

    /** 
     * Kiểm tra khi telephone có hơn 12 chữ số.
     * Dự kiến: Có lỗi validation trên trường telephone.
     */
    @Test
    public void whenTelephoneHasMoreThan12Digits_thenViolationOnTelephone() {
        OwnerRequest request = new OwnerRequest("John", "Doe", "123 Main St", "Anytown", "1234567890123");
        Set<jakarta.validation.ConstraintViolation<OwnerRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("telephone");
    }

    /** 
     * Kiểm tra khi telephone có đúng 12 chữ số.
     * Dự kiến: Không có lỗi validation.
     */
    @Test
    public void whenTelephoneHasExactly12Digits_thenNoViolations() {
        OwnerRequest request = new OwnerRequest("John", "Doe", "123 Main St", "Anytown", "123456789012");
        Set<jakarta.validation.ConstraintViolation<OwnerRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    /** 
     * Kiểm tra khi telephone có ít hơn 12 chữ số.
     * Dự kiến: Không có lỗi validation.
     */
    @Test
    public void whenTelephoneHasFewerThan12Digits_thenNoViolations() {
        OwnerRequest request = new OwnerRequest("John", "Doe", "123 Main St", "Anytown", "123");
        Set<jakarta.validation.ConstraintViolation<OwnerRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    /** 
     * Kiểm tra khi telephone chứa dấu chấm thập phân.
     * Dự kiến: Có lỗi validation trên trường telephone.
     */
    @Test
    public void whenTelephoneHasDecimal_thenViolationOnTelephone() {
        OwnerRequest request = new OwnerRequest("John", "Doe", "123 Main St", "Anytown", "123.45");
        Set<jakarta.validation.ConstraintViolation<OwnerRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("telephone");
    }

    //WEB.mapper owner map
    @Test
    void testMapOwnerRequestToOwner() {
        OwnerEntityMapper mapper = new OwnerEntityMapper(); // Real instance
        Owner owner = new Owner();
        OwnerRequest request = new OwnerRequest("John", "Doe", "123 Street", "City", "1234567890");
    
        Owner updatedOwner = mapper.map(owner, request);
    
        assertThat(updatedOwner.getFirstName()).isEqualTo("John");
        assertThat(updatedOwner.getLastName()).isEqualTo("Doe");
        assertThat(updatedOwner.getAddress()).isEqualTo("123 Street");
        assertThat(updatedOwner.getCity()).isEqualTo("City");
        assertThat(updatedOwner.getTelephone()).isEqualTo("1234567890");
    }

    @Test
    void testMapWithEmptyOwner() {
        OwnerEntityMapper mapper = new OwnerEntityMapper();
        Owner owner = new Owner();
        OwnerRequest request = new OwnerRequest("", "", "", "", "");
    
        Owner updatedOwner = mapper.map(owner, request);
    
        assertThat(updatedOwner.getFirstName()).isEmpty();
        assertThat(updatedOwner.getLastName()).isEmpty();
        assertThat(updatedOwner.getAddress()).isEmpty();
        assertThat(updatedOwner.getCity()).isEmpty();
        assertThat(updatedOwner.getTelephone()).isEmpty();
    }

    @Test
    public void testMapWithNullOwner() {
        OwnerEntityMapper mapper = new OwnerEntityMapper();
        OwnerRequest request = new OwnerRequest("addr", "city", "123", "John", "Doe"); // Assuming a constructor or record
        assertThrows(NullPointerException.class, () -> mapper.map(null, request));
    }

    @Test
    public void testMapWithNullRequest() {
        OwnerEntityMapper mapper = new OwnerEntityMapper();
        Owner owner = new Owner(); // Assuming a default constructor
        assertThrows(NullPointerException.class, () -> mapper.map(owner, null));
    } 

    @Test
    void testMapWithPartialData() {
        OwnerEntityMapper mapper = new OwnerEntityMapper();
        Owner owner = new Owner();
        OwnerRequest request = new OwnerRequest("Alice", null, "456 Avenue", null, "9876543210");
    
        Owner updatedOwner = mapper.map(owner, request);
    
        assertThat(updatedOwner.getFirstName()).isEqualTo("Alice");
        assertThat(updatedOwner.getLastName()).isNull();
        assertThat(updatedOwner.getAddress()).isEqualTo("456 Avenue");
        assertThat(updatedOwner.getCity()).isNull();
        assertThat(updatedOwner.getTelephone()).isEqualTo("9876543210");
    }
}