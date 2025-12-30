package com.techmate.techmate.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techmate.techmate.dto.MovementsDTO;
import com.techmate.techmate.dto.MovementResponse;
import com.techmate.techmate.hexagonal.domain.entity.MoveType;
import com.techmate.techmate.security.TokenUtils;
import com.techmate.techmate.service.MovementsService;
import com.techmate.techmate.service.movements.mapper.MovementsMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.Authentication;

@WebMvcTest(controllers = MovementsController.class)
@AutoConfigureMockMvc(addFilters = false) // Deshabilita filtros de seguridad reales para testing aislado
class MovementsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovementsService movementsService;

    @MockitoBean
    private MovementsMapper movementsMapper;

    // Mock estático para TokenUtils
    private MockedStatic<TokenUtils> tokenUtilsMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inicializar el mock estático para evitar el 403 en el controlador
        tokenUtilsMock = mockStatic(TokenUtils.class);
    }

    @AfterEach
    void tearDown() {
        // Cerrar el mock estático es CRÍTICO para no afectar otros tests
        tokenUtilsMock.close();
    }

    @Test
    void getMovementById_returnsMovementResponse() throws Exception {
        MovementsDTO dto = new MovementsDTO();
        dto.setId(1);
        dto.setQuantity(5);
        dto.setMoveType(MoveType.STOCK_ADD);

        MovementResponse resp = new MovementResponse(1, MoveType.STOCK_ADD, 5, new java.util.Date(), "", 1, "Admin", 2,
                "MaterialName");

        when(movementsService.getMovementsByID(1)).thenReturn(dto);
        when(movementsMapper.toResponse(eq(dto))).thenReturn(resp);

        mockMvc.perform(get("/admin/movement/1").param("id", "1")
                .contentType(MediaType.APPLICATION_JSON))
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

        MovementResponse resp = new MovementResponse(10, MoveType.BORROW, 3, new java.util.Date(), "test", 5, "Admin",
                2, "MaterialName");

        // Mockear el comportamiento del servicio
        when(movementsService.createMovementsDTO(any(MovementsDTO.class), eq(5))).thenReturn(created);
        when(movementsMapper.toResponse(eq(created))).thenReturn(resp);

        // Mockear Authentication
        Authentication mockAuth = org.mockito.Mockito.mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("user@example.com");
        when(mockAuth.getCredentials()).thenReturn("dummy-token");
        when(mockAuth.isAuthenticated()).thenReturn(true);

        // 🟢 Configurar comportamiento estático de TokenUtils para este test
        tokenUtilsMock.when(TokenUtils::getAuthenticatedUserRole).thenReturn("ADMIN");
        // ESTA es la clave para que extractUserIdFromAuthentication no falle:
        tokenUtilsMock.when(() -> TokenUtils.getUserIdFromToken("dummy-token")).thenReturn(5);

        mockMvc.perform(post("/admin/movement/create")
                .header("Authorization", "Bearer dummy-token")
                .param("quantity", "3")
                .param("moveType", "OUT")
                .param("id_material", "2")
                .param("comment", "test")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .principal(mockAuth)) // Inyectar el principal mockeado
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.moveType").value("OUT"));
    }
}
