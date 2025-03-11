package org.springframework.samples.petclinic.genai.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VisitDetailsTest {

    @Test
    public void testConstructorAndGetter() {
        VisitDetails visit = new VisitDetails(1, 101, "2025-03-09", "Routine checkup");

        assertEquals(1, visit.id());
        assertEquals(101, visit.petId());
        assertEquals("2025-03-09", visit.date());
        assertEquals("Routine checkup", visit.description());
    }

    @Test
    public void testEqualsAndHashCode() {
        VisitDetails visit1 = new VisitDetails(1, 101, "2025-03-09", "Routine checkup");
        VisitDetails visit2 = new VisitDetails(1, 101, "2025-03-09", "Routine checkup");
        VisitDetails visit3 = new VisitDetails(2, 102, "2025-04-10", "Vaccination");

        assertEquals(visit1, visit2);
        assertNotEquals(visit1, visit3);
        assertEquals(visit1.hashCode(), visit2.hashCode());
    }

    @Test
    public void testToString() {
        VisitDetails visit = new VisitDetails(1, 101, "2025-03-09", "Routine checkup");

        String result = visit.toString();
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("petId=101"));
        assertTrue(result.contains("date=2025-03-09"));
        assertTrue(result.contains("description=Routine checkup"));
    }

    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        VisitDetails visit = new VisitDetails(1, 101, "2025-03-09", "Routine checkup");
        String json = objectMapper.writeValueAsString(visit);

        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"petId\":101"));
        assertTrue(json.contains("\"date\":\"2025-03-09\""));
        assertTrue(json.contains("\"description\":\"Routine checkup\""));
    }
}
