package org.springframework.samples.petclinic.genai;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.samples.petclinic.genai.dto.*;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AIFunctionConfigurationTest {

    @Mock
    private AIDataProvider petclinicAiProvider;

    @InjectMocks
    private AIFunctionConfiguration aiFunctionConfiguration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Test danh sách chủ sở hữu bình thường
    @Test
    void testListOwners_Success() {
        OwnerDetails ownerDetails = new OwnerDetails(1, "John", "Doe", "123 Main St", "Springfield", "1234567890", Collections.emptyList());
        OwnersResponse mockResponse = new OwnersResponse(List.of(ownerDetails));
        when(petclinicAiProvider.getAllOwners()).thenReturn(mockResponse);

        Function<OwnerRequest, OwnersResponse> listOwnersFunction = aiFunctionConfiguration.listOwners(petclinicAiProvider);
        OwnerRequest request = new OwnerRequest("John", "Doe", "123 Main St", "Springfield", "1234567890");
        OwnersResponse response = listOwnersFunction.apply(request);

        assertNotNull(response);
        assertEquals(1, response.owners().size());
        verify(petclinicAiProvider).getAllOwners();
    }

    // ✅ Test danh sách chủ sở hữu rỗng
    @Test
    void testListOwners_EmptyList() {
        when(petclinicAiProvider.getAllOwners()).thenReturn(new OwnersResponse(Collections.emptyList()));

        Function<OwnerRequest, OwnersResponse> listOwnersFunction = aiFunctionConfiguration.listOwners(petclinicAiProvider);
        OwnerRequest request = new OwnerRequest("John", "Doe", "123 Main St", "Springfield", "1234567890");
        OwnersResponse response = listOwnersFunction.apply(request);

        assertNotNull(response);
        assertTrue(response.owners().isEmpty());
        verify(petclinicAiProvider).getAllOwners();
    }

    // ✅ Test thêm chủ sở hữu thành công
    @Test
    void testAddOwnerToPetclinic_Success() {
        OwnerRequest request = new OwnerRequest("Jane", "Smith", "456 Elm St", "Springfield", "0987654321");
        OwnerDetails ownerDetails = new OwnerDetails(2, "Jane", "Smith", "456 Elm St", "Springfield", "0987654321", Collections.emptyList());
        OwnerResponse mockResponse = new OwnerResponse(ownerDetails);
        when(petclinicAiProvider.addOwnerToPetclinic(request)).thenReturn(mockResponse);

        Function<OwnerRequest, OwnerResponse> addOwnerFunction = aiFunctionConfiguration.addOwnerToPetclinic(petclinicAiProvider);
        OwnerResponse response = addOwnerFunction.apply(request);

        assertNotNull(response);
        assertEquals("Jane", response.owner().firstName());
        verify(petclinicAiProvider).addOwnerToPetclinic(request);
    }

    // ✅ Test danh sách bác sĩ thú y bình thường
    @Test
    void testListVets_Success() throws JsonProcessingException {
        Vet vet = new Vet(1, "James", "Carter", Collections.emptySet());
        VetRequest request = new VetRequest(vet);
        VetResponse mockResponse = new VetResponse(List.of("Vet1", "Vet2"));
        when(petclinicAiProvider.getVets(request)).thenReturn(mockResponse);

        Function<VetRequest, VetResponse> listVetsFunction = aiFunctionConfiguration.listVets(petclinicAiProvider);
        VetResponse response = listVetsFunction.apply(request);

        assertNotNull(response);
        assertEquals(2, response.vet().size());
        verify(petclinicAiProvider).getVets(request);
    }

    // ✅ Test danh sách bác sĩ thú y rỗng
    @Test
    void testListVets_EmptyList() throws JsonProcessingException {
        Vet vet = new Vet(1, "James", "Carter", Collections.emptySet());
        VetRequest request = new VetRequest(vet);
        when(petclinicAiProvider.getVets(request)).thenReturn(new VetResponse(Collections.emptyList()));

        Function<VetRequest, VetResponse> listVetsFunction = aiFunctionConfiguration.listVets(petclinicAiProvider);
        VetResponse response = listVetsFunction.apply(request);

        assertNotNull(response);
        assertTrue(response.vet().isEmpty());
        verify(petclinicAiProvider).getVets(request);
    }

    // ✅ Test lỗi JSON khi lấy danh sách bác sĩ thú y
    @Test
    void testListVets_JsonProcessingException() throws JsonProcessingException {
        Vet vet = new Vet(1, "James", "Carter", Collections.emptySet());
        VetRequest request = new VetRequest(vet);
        when(petclinicAiProvider.getVets(request)).thenThrow(new JsonProcessingException("Test exception") {});

        Function<VetRequest, VetResponse> listVetsFunction = aiFunctionConfiguration.listVets(petclinicAiProvider);
        VetResponse response = listVetsFunction.apply(request);

        assertNull(response, "Response should be null when JsonProcessingException occurs");
        verify(petclinicAiProvider).getVets(request);
    }

    // ✅ Test thêm thú cưng thành công
    @Test
    void testAddPetToOwner_Success() {
        PetRequest petRequest = new PetRequest(1, null, "Buddy", 2);
        AddPetRequest addPetRequest = new AddPetRequest(petRequest, 1);
        PetDetails petDetails = new PetDetails(1, "Buddy", "2024-01-01", new PetType("dog"), Collections.emptyList());
        AddedPetResponse mockResponse = new AddedPetResponse(petDetails);
        when(petclinicAiProvider.addPetToOwner(addPetRequest)).thenReturn(mockResponse);

        Function<AddPetRequest, AddedPetResponse> addPetFunction = aiFunctionConfiguration.addPetToOwner(petclinicAiProvider);
        AddedPetResponse response = addPetFunction.apply(addPetRequest);

        assertNotNull(response);
        assertEquals("Buddy", response.pet().name());
        verify(petclinicAiProvider).addPetToOwner(addPetRequest);
    }

    // ✅ Test lỗi khi thêm thú cưng
    @Test
    void testAddPetToOwner_Failure() {
        PetRequest petRequest = new PetRequest(1, null, "Buddy", 2);
        AddPetRequest addPetRequest = new AddPetRequest(petRequest, 1);
        when(petclinicAiProvider.addPetToOwner(addPetRequest)).thenReturn(null);

        Function<AddPetRequest, AddedPetResponse> addPetFunction = aiFunctionConfiguration.addPetToOwner(petclinicAiProvider);
        AddedPetResponse response = addPetFunction.apply(addPetRequest);

        assertNull(response, "Response should be null when addPetToOwner fails");
        verify(petclinicAiProvider).addPetToOwner(addPetRequest);
    }
}
