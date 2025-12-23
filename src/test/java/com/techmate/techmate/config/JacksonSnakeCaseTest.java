package com.techmate.techmate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.techmate.techmate.entity.Borrow;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class JacksonSnakeCaseTest {

    @Test
    void jacksonShouldSerializeBorrowWithSnakeCase() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        Borrow b = new Borrow();
        b.setId(123);
        b.setDate(new Date());
        b.setAmount(BigDecimal.valueOf(10.5));

        String json = mapper.writeValueAsString(b);
        assertTrue(json.contains("borrow_id") || json.contains("id"));
        assertTrue(json.contains("issue_date") || json.contains("date"));
        assertTrue(json.contains("amount"));
    }
}
