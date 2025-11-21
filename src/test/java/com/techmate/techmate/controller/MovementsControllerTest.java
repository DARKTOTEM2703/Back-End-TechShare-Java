package com.techmate.techmate.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techmate.techmate.dto.MovementsDTO;
import com.techmate.techmate.dto.MovementResponse;
import com.techmate.techmate.entity.MoveType;
import com.techmate.techmate.service.MovementsService;
import com.techmate.techmate.service.movements.mapper.MovementsMapper;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.techmate.techmate.testutils.JWTTestHelper;
import org.springframework.security.core.Authentication;

@WebMvcTest(controllers = MovementsController.class)
@AutoConfigureMockMvc(addFilters = false)
class MovementsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovementsService movementsService;

    @MockitoBean
    private MovementsMapper movementsMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getMovementById_returnsMovementResponse() throws Exception {
        MovementsDTO dto = new MovementsDTO();
        dto.setId(1);
        dto.setQuantity(5);
    dto.setMoveType(MoveType.STOCK_ADD);

    MovementResponse resp = new MovementResponse(1, MoveType.STOCK_ADD, 5, new java.util.Date(), "", 1, "Admin", 2, "MaterialName");

        when(movementsService.getMovementsByID(1)).thenReturn(dto);
        when(movementsMapper.toResponse(eq(dto))).thenReturn(resp);

        // call path /admin/movement/1 and include request param id=1 to satisfy the controller signature
        mockMvc.perform(get("/admin/movement/1").param("id", "1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.quantity").value(5));
    }

    @Test
    void createMovement_handlesParamsAndReturnsCreated() throws Exception {
        MovementsDTO reqDto = new MovementsDTO();
        reqDto.setQuantity(3);
    reqDto.setMoveType(MoveType.BORROW);
        reqDto.setId(2);

        MovementsDTO created = new MovementsDTO();
        created.setId(10);
        created.setQuantity(3);
    created.setMoveType(MoveType.BORROW);
        created.setId(2);

    MovementResponse resp = new MovementResponse(10, MoveType.BORROW, 3, new java.util.Date(), "test", 5, "Admin", 2, "MaterialName");

        String token = JWTTestHelper.createTokenWithRoles(5, "user@example.com", "user", "USER");
        when(movementsService.createMovementsDTO(any(MovementsDTO.class), eq(5))).thenReturn(created);
        when(movementsMapper.toResponse(eq(created))).thenReturn(resp);

        // Mock Authentication object since security filters are disabled
        Authentication mockAuth = org.mockito.Mockito.mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("user@example.com");
        when(mockAuth.getCredentials()).thenReturn(token);

        mockMvc.perform(post("/admin/movement/create")
                .header("Authorization", "Bearer " + token)
                .param("quantity", "3")
                .param("moveType", "OUT")
                .param("id_material", "2")
                .param("comment", "test")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .principal(mockAuth)) // Add the mocked Authentication
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.move_type").value("OUT"));
    }
}

