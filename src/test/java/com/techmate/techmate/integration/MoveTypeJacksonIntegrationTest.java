package com.techmate.techmate.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmate.techmate.entity.MoveType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class MoveTypeJacksonIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    public static class Payload {
        private MoveType moveType;
        private Integer id;

        public MoveType getMoveType() {
            return moveType;
        }

        public void setMoveType(MoveType moveType) {
            this.moveType = moveType;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        // Backward compatibility method for legacy code
        public Integer getMaterialsId() {
            return id;
        }

        public void setMaterialsId(Integer materialsId) {
            this.id = materialsId;
        }
    }

    @Test
    public void jacksonSerializesToSnakeCaseAndMoveTypeLegacyToken() throws Exception {
        Payload p = new Payload();
        p.setMoveType(MoveType.BORROW);
        p.setId(2);

        String json = objectMapper.writeValueAsString(p);

        // Debe usar snake_case
        assertTrue(json.contains("\"move_type\""), "Debe serializar campo como move_type");
        assertTrue(json.contains("\"id\":2"), "id debe incluir el valor");

        // Deserialización desde la API legacy
        String input = "{\"move_type\":\"BORROW\",\"id\":2}";
        Payload des = objectMapper.readValue(input, Payload.class);
        assertEquals(MoveType.BORROW, des.getMoveType());
        assertEquals(2, des.getId());
    }
}
