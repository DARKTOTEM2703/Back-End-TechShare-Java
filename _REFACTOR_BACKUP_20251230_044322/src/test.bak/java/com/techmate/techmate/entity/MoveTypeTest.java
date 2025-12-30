package com.techmate.techmate.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class MoveTypeTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void serializeLoan_shouldReturnOUT() throws Exception {
        String json = mapper.writeValueAsString(MoveType.BORROW);
        // JSON string with quotes
        assertEquals("\"OUT\"", json);
    }

    @Test
    public void deserializeOut_shouldReturnLoan() throws Exception {
        MoveType t = mapper.readValue("\"OUT\"", MoveType.class);
        assertEquals(MoveType.BORROW, t);
    }

    @Test
    public void deserializeIn_shouldReturnReturn() throws Exception {
        MoveType t = mapper.readValue("\"IN\"", MoveType.class);
        assertEquals(MoveType.RETURN, t);
    }

    @Test
    public void unknownValue_shouldReturnNull() throws Exception {
        MoveType t = mapper.readValue("\"UNKNOWN_VAL\"", MoveType.class);
        assertNull(t);
    }
}

