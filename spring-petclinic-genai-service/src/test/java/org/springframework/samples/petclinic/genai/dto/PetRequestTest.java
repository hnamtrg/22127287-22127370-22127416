package org.springframework.samples.petclinic.genai.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class PetRequestTest {

    @Test
    public void testConstructorAndGetters() {
        Date birthDate = new Date();
        PetRequest petRequest = new PetRequest(1, birthDate, "Buddy", 2);

        assertEquals(1, petRequest.id());
        assertEquals(birthDate, petRequest.birthDate());
        assertEquals("Buddy", petRequest.name());
        assertEquals(2, petRequest.typeId());
    }

    @Test
    public void testEqualsAndHashCode() {
        Date birthDate = new Date();
        PetRequest pet1 = new PetRequest(1, birthDate, "Buddy", 2);
        PetRequest pet2 = new PetRequest(1, birthDate, "Buddy", 2);
        PetRequest pet3 = new PetRequest(2, birthDate, "Charlie", 3);

        assertEquals(pet1, pet2);
        assertNotEquals(pet1, pet3);
        assertEquals(pet1.hashCode(), pet2.hashCode());
    }

    @Test
    public void testToString() {
        Date birthDate = new Date();
        PetRequest petRequest = new PetRequest(1, birthDate, "Buddy", 2);

        String expected = "PetRequest[id=1, birthDate=" + birthDate + ", name=Buddy, typeId=2]";
        assertTrue(petRequest.toString().contains("id=1"));
        assertTrue(petRequest.toString().contains("name=Buddy"));
        assertTrue(petRequest.toString().contains("typeId=2"));
    }

    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

        Date birthDate = new Date();
        PetRequest petRequest = new PetRequest(1, birthDate, "Buddy", 2);
        String json = objectMapper.writeValueAsString(petRequest);

        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"name\":\"Buddy\""));
        assertTrue(json.contains("\"typeId\":2"));
    }
}
