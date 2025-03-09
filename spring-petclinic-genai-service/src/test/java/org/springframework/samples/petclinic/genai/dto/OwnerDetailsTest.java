package org.springframework.samples.petclinic.genai.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OwnerDetailsTest {

    @Test
    public void testConstructorAndGetter() {
        PetType dog = new PetType("Dog");
        VisitDetails visit = new VisitDetails(1, 1, "2024-03-01", "Routine Checkup");
        PetDetails pet = new PetDetails(1, "Buddy", "2023-01-01", dog, List.of(visit));

        OwnerDetails owner = new OwnerDetails(1, "John", "Doe", "123 Main St", "Springfield", "1234567890", List.of(pet));

        assertEquals(1, owner.id());
        assertEquals("John", owner.firstName());
        assertEquals("Doe", owner.lastName());
        assertEquals("123 Main St", owner.address());
        assertEquals("Springfield", owner.city());
        assertEquals("1234567890", owner.telephone());
        assertEquals(1, owner.pets().size());
    }

    @Test
    public void testEqualsAndHashCode() {
        OwnerDetails owner1 = new OwnerDetails(1, "John", "Doe", "123 Main St", "Springfield", "1234567890", List.of());
        OwnerDetails owner2 = new OwnerDetails(1, "John", "Doe", "123 Main St", "Springfield", "1234567890", List.of());
        OwnerDetails owner3 = new OwnerDetails(2, "Jane", "Smith", "456 Elm St", "Shelbyville", "0987654321", List.of());

        assertEquals(owner1, owner2);
        assertNotEquals(owner1, owner3);
        assertEquals(owner1.hashCode(), owner2.hashCode());
    }

    @Test
    public void testToString() {
        OwnerDetails owner = new OwnerDetails(1, "John", "Doe", "123 Main St", "Springfield", "1234567890", List.of());

        String result = owner.toString();
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("firstName=John"));
        assertTrue(result.contains("lastName=Doe"));
        assertTrue(result.contains("address=123 Main St"));
        assertTrue(result.contains("city=Springfield"));
        assertTrue(result.contains("telephone=1234567890"));
    }

    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        OwnerDetails owner = new OwnerDetails(1, "John", "Doe", "123 Main St", "Springfield", "1234567890", List.of());

        String json = objectMapper.writeValueAsString(owner);

        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"firstName\":\"John\""));
        assertTrue(json.contains("\"lastName\":\"Doe\""));
        assertTrue(json.contains("\"address\":\"123 Main St\""));
        assertTrue(json.contains("\"city\":\"Springfield\""));
        assertTrue(json.contains("\"telephone\":\"1234567890\""));
    }
}
