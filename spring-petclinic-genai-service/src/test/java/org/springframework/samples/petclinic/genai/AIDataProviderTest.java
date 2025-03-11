package org.springframework.samples.petclinic.genai;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.samples.petclinic.genai.dto.OwnerDetails;
import org.springframework.samples.petclinic.genai.dto.PetDetails;
import org.springframework.samples.petclinic.genai.dto.PetRequest;
import org.springframework.samples.petclinic.genai.dto.Specialty;
import org.springframework.samples.petclinic.genai.dto.Vet;
import org.springframework.samples.petclinic.genai.dto.PetType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class AIDataProviderTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private VectorStore vectorStore;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private AIDataProvider aiDataProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(webClientBuilder.build()).thenReturn(webClient);
        aiDataProvider = new AIDataProvider(webClientBuilder, vectorStore);

        // Mock hành vi của webClient
        when(webClient.get()).thenAnswer(invocation -> requestHeadersUriSpec);
        when(webClient.post()).thenAnswer(invocation -> requestBodyUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenAnswer(invocation -> responseSpec);
        when(requestBodyUriSpec.retrieve()).thenAnswer(invocation -> responseSpec);
    }

    @Test
    void testGetAllOwners() {
        // Arrange
        OwnerDetails owner = new OwnerDetails(1, "John", "Doe", "123 Street", "City", "1234567890", List.of());
        List<OwnerDetails> mockOwners = List.of(owner);
        when(requestHeadersUriSpec.uri(anyString())).thenAnswer(invocation -> requestHeadersUriSpec);
        when(responseSpec.bodyToMono(new ParameterizedTypeReference<List<OwnerDetails>>() {})).thenReturn(Mono.just(mockOwners));

        // Act
        OwnersResponse response = aiDataProvider.getAllOwners();

        // Assert
        assertNotNull(response);
        assertEquals(1, response.owners().size());
        assertEquals("John", response.owners().get(0).firstName());
    }

    // @Test
    // void testGetVets() throws Exception {
    //     // Arrange
    //     Specialty specialty = new Specialty("Surgery"); // Giả định Specialty chỉ có name
    //     Vet vet = new Vet(1, "John", "Doe", Set.of(specialty));
    //     VetRequest request = new VetRequest(vet);
    //     Document doc1 = new Document("Vet John Doe specializes in Surgery.");
    //     Document doc2 = new Document("Dr. John Doe is an expert in Surgery.");
    //     when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(doc1, doc2));

    //     // Act
    //     VetResponse response = aiDataProvider.getVets(request);

    //     // Assert
    //     assertNotNull(response);
    //     assertEquals(2, response.vet().size());
    //     assertTrue(response.vet().contains("Vet John Doe specializes in Surgery."));
    // }

    // @Test
    // void testAddPetToOwner() throws ParseException {
    //     // Arrange
    //     PetType petType = new PetType(2, "Dog"); // Giả định PetType có id và name
    //     PetDetails petDetails = new PetDetails(1, "Buddy", "2023-01-01", petType, List.of());
    //     PetRequest petRequest = new PetRequest("Buddy", "2023-01-01", petType); // Giả định constructor này
    //     AddPetRequest request = new AddPetRequest(petRequest, 1);
    //     when(requestBodyUriSpec.uri(anyString())).thenAnswer(invocation -> requestBodyUriSpec);
    //     when(requestBodyUriSpec.bodyValue(any())).thenAnswer(invocation -> requestBodyUriSpec);
    //     when(responseSpec.bodyToMono(PetDetails.class)).thenReturn(Mono.just(petDetails));

    //     // Act
    //     AddedPetResponse response = aiDataProvider.addPetToOwner(request);

    //     // Assert
    //     assertNotNull(response);
    //     assertEquals("Buddy", response.pet().name());
    // }

    @Test
    void testAddOwnerToPetclinic() {
        // Arrange
        OwnerDetails ownerDetails = new OwnerDetails(1, "Jane", "Smith", "456 Avenue", "Another City", "9876543210", new ArrayList<>());
        OwnerRequest request = new OwnerRequest("Jane", "Smith", "456 Avenue", "Another City", "9876543210");
        when(requestBodyUriSpec.uri(anyString())).thenAnswer(invocation -> requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenAnswer(invocation -> requestBodyUriSpec);
        when(responseSpec.bodyToMono(OwnerDetails.class)).thenReturn(Mono.just(ownerDetails));

        // Act
        OwnerResponse response = aiDataProvider.addOwnerToPetclinic(request);

        // Assert
        assertNotNull(response);
        assertEquals("Jane", response.owner().firstName());
    }
}