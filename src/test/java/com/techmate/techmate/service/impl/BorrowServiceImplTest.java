package com.techmate.techmate.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.techmate.techmate.dto.BorrowDTO;
import com.techmate.techmate.dto.DetailsBorrowDTO;
import com.techmate.techmate.entity.*;
import com.techmate.techmate.repository.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 🧪 Pruebas unitarias para BorrowServiceImpl
 * 
 * Cubre todos los escenarios críticos del sistema de préstamos:
 * - ✅ Actualización de estados de préstamos
 * - ✅ Manejo automático de stock de materiales
 * - ✅ Validaciones de negocio
 * - ✅ Filtros por estado y fecha
 * - ✅ Manejo de errores y excepciones
 * 
 * @author TechShare Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("🏦 BorrowService - Gestión de Préstamos")
class BorrowServiceImplTest {

    @Mock
    private BorrowRepository borrowRepository;
    
    @Mock
    private MaterialsRepository materialsRepository;
    
    @Mock
    private DetailsBorrowRepository detailsBorrowRepository;
    
    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private com.techmate.techmate.service.borrow.processor.BorrowStateProcessor borrowStateProcessor;

    @Mock
    private com.techmate.techmate.service.borrow.manager.IBorrowStockManager borrowStockManager;

    @Mock
    private org.springframework.context.ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private BorrowServiceImpl borrowService;

    // 📋 Datos de prueba
    private Borrow testBorrow;
    private BorrowDTO testBorrowDTO;
    private Usuario testUser;
    private Usuario testAdmin;
    private Materials testMaterial;
    private DetailsBorrow testDetail;
    private DetailsBorrowDTO testDetailDTO;

    @BeforeEach
    void setUp() {
        // 👤 Configurar usuarios de prueba
        testUser = new Usuario();
        testUser.setId(100);
        testUser.setUser_name("juan_perez");
        testUser.setFirst_name("Juan");
        testUser.setLast_name("Pérez");
        testUser.setEmail("juan.perez@techshare.com");

        testAdmin = new Usuario();
        testAdmin.setId(200);
        testAdmin.setUser_name("admin_maria");
        testAdmin.setFirst_name("María");
        testAdmin.setLast_name("González");
        testAdmin.setEmail("maria.admin@techshare.com");

        // 📦 Configurar material de prueba
        testMaterial = new Materials();
        testMaterial.setId(10);
        testMaterial.setName("Arduino UNO R3");
        testMaterial.setDescription("Microcontrolador para proyectos IoT");
        testMaterial.setPrice(25.99);
        testMaterial.setBorrowable_stock(15); // Stock disponible para préstamos
        testMaterial.setStock(20); // Stock total

        // 📋 Configurar detalle de préstamo
        testDetail = new DetailsBorrow();
        testDetail.setId(1);
        testDetail.setQuantity(3); // Solicita 3 unidades
        testDetail.setUnitPrice(25.99);
        testDetail.setTotalPrice(77.97); // 3 * 25.99
        testDetail.setMaterials(testMaterial);

        // 📖 Configurar préstamo de prueba
        testBorrow = new Borrow();
        testBorrow.setId(1);
        testBorrow.setDate(new Date());
    testBorrow.setStatus(Status.PENDING); // Estado inicial
        testBorrow.setAmount(77.97);
        testBorrow.setUsuario(testUser);
        testBorrow.setAdmin(testAdmin);
        testBorrow.setDetails(new ArrayList<>(Arrays.asList(testDetail)));
        
        testDetail.setBorrow(testBorrow); // Relación bidireccional

        // 📝 Configurar DTO de prueba
        testDetailDTO = new DetailsBorrowDTO();
        testDetailDTO.setId(10);
        testDetailDTO.setQuantity(3);
        testDetailDTO.setUnitPrice(25.99);
        testDetailDTO.setTotalPrice(77.97);

        testBorrowDTO = new BorrowDTO();
        testBorrowDTO.setId(1);
        testBorrowDTO.setDate(new Date());
    testBorrowDTO.setStatus(Status.PENDING);
        testBorrowDTO.setAmount(77.97);
        testBorrowDTO.setUsuarioId(100);
        testBorrowDTO.setAdminId(200);
        testBorrowDTO.setDetails(Arrays.asList(testDetailDTO));

        // Stubs por defecto para el stock manager y processor
        when(borrowStockManager.getAvailableStock(anyInt())).thenAnswer(inv -> testMaterial.getBorrowable_stock());
        // Validación dinámica: validar disponibilidad lanzando excepción si se solicita más de lo disponible
        doAnswer(inv -> {
            Integer materialId = inv.getArgument(0);
            Integer qty = inv.getArgument(1);
            if (materialId.equals(testMaterial.getId())) {
                if (testMaterial.getBorrowable_stock() < qty) {
                    throw com.techmate.techmate.exception.BorrowBusinessException.insufficientStock(materialId, qty, testMaterial.getBorrowable_stock());
                }
            }
            return null;
        }).when(borrowStockManager).validateStockAvailability(anyInt(), anyInt());

        // Reducir stock en el objeto de prueba cuando se invoque reduceStock
        doAnswer(inv -> {
            Integer materialId = inv.getArgument(0);
            Integer qty = inv.getArgument(1);
            if (materialId.equals(testMaterial.getId())) {
                testMaterial.setBorrowable_stock(testMaterial.getBorrowable_stock() - qty);
            }
            return null;
        }).when(borrowStockManager).reduceStock(anyInt(), anyInt());

        // Restaurar stock en el objeto de prueba cuando se invoque restoreStock
        doAnswer(inv -> {
            Integer materialId = inv.getArgument(0);
            Integer qty = inv.getArgument(1);
            if (materialId.equals(testMaterial.getId())) {
                testMaterial.setBorrowable_stock(testMaterial.getBorrowable_stock() + qty);
            }
            return null;
        }).when(borrowStockManager).restoreStock(anyInt(), anyInt());

        // El processor no necesita un stub específico aquí; las pruebas usan el servicio directamente
    }

    // ✅ === TESTS DE ACTUALIZACIÓN DE ESTADO ===

    @Test
    @DisplayName("✅ Aprobar préstamo: PROCESS → BORROWED")
    void updateBorrowStatus_ProcessToBorrowed_Success() throws Exception {
        // Given: Préstamo en estado PROCESS con stock suficiente
        when(borrowRepository.findById(1)).thenReturn(Optional.of(testBorrow));
        when(usuarioRepository.findById(200)).thenReturn(Optional.of(testAdmin));
        when(borrowRepository.save(any(Borrow.class))).thenReturn(testBorrow);

        // When: Admin aprueba el préstamo
    borrowService.updateBorrowStatus(1, Status.BORROWED, 200);

    // Then: Estado cambia a LOANED and stock se reduce
        verify(borrowRepository).findById(1);
        verify(usuarioRepository).findById(200);
    verify(borrowStockManager).reduceStock(anyInt(), anyInt());
    verify(borrowRepository).save(any(Borrow.class));
        
        // ✅ Verificar que se publica BorrowCreatedEvent
        verify(eventPublisher).publishEvent(any(com.techmate.techmate.event.BorrowCreatedEvent.class));
        
        // Verificar que el stock se redujo correctamente
        assertThat(testMaterial.getBorrowable_stock()).isEqualTo(12); // 15 - 3 = 12
        assertThat(testBorrow.getStartDate()).isNotNull();
    assertThat(testBorrow.getStatus()).isEqualTo(Status.BORROWED);
    }

    @Test
    @DisplayName("❌ Rechazar préstamo: PROCESS → REJECTED")
    void updateBorrowStatus_ProcessToRejected_Success() throws Exception {
        // Given: Préstamo en estado PROCESS
        when(borrowRepository.findById(1)).thenReturn(Optional.of(testBorrow));
        when(usuarioRepository.findById(200)).thenReturn(Optional.of(testAdmin));
        when(borrowRepository.save(any(Borrow.class))).thenReturn(testBorrow);

        // When: Admin rechaza el préstamo
        borrowService.updateBorrowStatus(1, Status.REJECTED, 200);

        // Then: Estado cambia a REJECTED, se establece endDate
    verify(borrowRepository).save(any(Borrow.class));
        assertThat(testBorrow.getStatus()).isEqualTo(Status.REJECTED);
        assertThat(testBorrow.getEndDate()).isNotNull();
        
        // ✅ NO debe publicar evento cuando se rechaza
        verify(eventPublisher, never()).publishEvent(any());
        
        // Stock NO debe cambiar cuando se rechaza
        assertThat(testMaterial.getBorrowable_stock()).isEqualTo(15);
    }

    @Test
    @DisplayName("🔄 Devolver préstamo: BORROWED → RETURNED")
    void updateBorrowStatus_BorrowedToReturned_Success() throws Exception {
    // Given: Préstamo en estado LOANED (ya se había aprobado)
    testBorrow.setStatus(Status.BORROWED);
        testBorrow.setStartDate(new Date());
        testMaterial.setBorrowable_stock(12); // Ya se había reducido de 15 a 12
        
        when(borrowRepository.findById(1)).thenReturn(Optional.of(testBorrow));
        when(usuarioRepository.findById(200)).thenReturn(Optional.of(testAdmin));
    when(borrowRepository.save(any(Borrow.class))).thenReturn(testBorrow);

        // When: Usuario devuelve el préstamo
        borrowService.updateBorrowStatus(1, Status.RETURNED, 200);

        // Then: Estado cambia a RETURNED, stock se restaura
    verify(borrowStockManager).restoreStock(anyInt(), anyInt());
    verify(borrowRepository).save(any(Borrow.class));
        
        // ✅ Verificar que se publica BorrowReturnedEvent
        verify(eventPublisher).publishEvent(any(com.techmate.techmate.event.BorrowReturnedEvent.class));
        
        assertThat(testBorrow.getStatus()).isEqualTo(Status.RETURNED);
        assertThat(testMaterial.getBorrowable_stock()).isEqualTo(15); // 12 + 3 = 15 (restaurado)
        assertThat(testBorrow.getReturnDate()).isNotNull();
        assertThat(testBorrow.getEndDate()).isNotNull();
    }

    @Test
    @DisplayName("⚠️ Error: Stock insuficiente para aprobar préstamo")
    void updateBorrowStatus_InsufficientStock_ThrowsException() {
        // Given: Préstamo solicita más de lo que hay disponible
        testMaterial.setBorrowable_stock(2); // Solo 2 disponibles, pero solicita 3
        
        when(borrowRepository.findById(1)).thenReturn(Optional.of(testBorrow));
        when(usuarioRepository.findById(200)).thenReturn(Optional.of(testAdmin));

        // When & Then: Debe lanzar excepción por stock insuficiente
    assertThatThrownBy(() -> borrowService.updateBorrowStatus(1, Status.BORROWED, 200))
            .isInstanceOf(com.techmate.techmate.exception.BorrowBusinessException.class)
            .hasMessageContaining("Stock insuficiente");
        
        // Verificar que NO se guardaron cambios
        verify(materialsRepository, never()).save(any());
        verify(borrowRepository, never()).save(any());
    }

    @Test
    @DisplayName("🔍 Error: Préstamo no encontrado")
    void updateBorrowStatus_BorrowNotFound_ThrowsException() {
        // Given: ID de préstamo inexistente
        when(borrowRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then: Debe lanzar excepción
    assertThatThrownBy(() -> borrowService.updateBorrowStatus(999, Status.BORROWED, 200))
            .isInstanceOf(com.techmate.techmate.exception.BusinessException.class)
            .hasMessageContaining("Préstamo no encontrado");
    }

    @Test
    @DisplayName("👤 Error: Administrador no encontrado")
    void updateBorrowStatus_AdminNotFound_ThrowsException() {
        // Given: ID de admin inexistente
        when(borrowRepository.findById(1)).thenReturn(Optional.of(testBorrow));
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then: Debe lanzar excepción
    assertThatThrownBy(() -> borrowService.updateBorrowStatus(1, Status.BORROWED, 999))
            .isInstanceOf(com.techmate.techmate.exception.BusinessException.class)
            .hasMessageContaining("Administrador no encontrado");
    }

    @Test
    @DisplayName("🚫 Error: Transición de estado inválida")
    void updateBorrowStatus_InvalidStateTransition_ThrowsException() {
        // Given: Préstamo ya devuelto (no se puede modificar)
        testBorrow.setStatus(Status.RETURNED);
        testBorrow.setReturnDate(new Date());
        
        when(borrowRepository.findById(1)).thenReturn(Optional.of(testBorrow));

        // When & Then: No se puede modificar préstamo devuelto
    assertThatThrownBy(() -> borrowService.updateBorrowStatus(1, Status.BORROWED, 200))
            .isInstanceOf(Exception.class)
            .hasMessageContaining("Solo se puede modificar el estado");
    }

    @Test
    @DisplayName("🔄 Error: Intentar devolver sin haber prestado")
    void updateBorrowStatus_ReturnWithoutBorrowedState_ThrowsException() {
    // Given: Préstamo en PENDING (no LOANED)
    testBorrow.setStatus(Status.PENDING);
        
        when(borrowRepository.findById(1)).thenReturn(Optional.of(testBorrow));
        when(usuarioRepository.findById(200)).thenReturn(Optional.of(testAdmin));

        // When & Then: No se puede devolver sin haber sido prestado
        assertThatThrownBy(() -> borrowService.updateBorrowStatus(1, Status.RETURNED, 200))
            .isInstanceOf(Exception.class)
            .hasMessageContaining("debe estar en estado BORROWED");
    }

    // 📋 === TESTS DE CONSULTAS Y FILTROS ===

    @Test
    @DisplayName("📖 Obtener todos los préstamos")
    void getAllBorrowDTO_Success() {
        // Given: Lista de préstamos en el sistema
        List<Borrow> borrows = Arrays.asList(testBorrow);
        when(borrowRepository.findAll()).thenReturn(borrows);

        // When: Obtener todos los préstamos
        List<BorrowDTO> result = borrowService.getAllBorrowDTO();

        // Then: Retorna lista completa de DTOs
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(testBorrow.getId());
        assertThat(result.get(0).getUsuarioId()).isEqualTo(100);
        assertThat(result.get(0).getAdminId()).isEqualTo(200);
        
        verify(borrowRepository).findAll();
    }

    @Test
    @DisplayName("📖 Lista vacía cuando no hay préstamos")
    void getAllBorrowDTO_EmptyList_Success() {
        // Given: No hay préstamos en el sistema
        when(borrowRepository.findAll()).thenReturn(Arrays.asList());

        // When: Obtener todos los préstamos
        List<BorrowDTO> result = borrowService.getAllBorrowDTO();

        // Then: Retorna lista vacía
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("🔍 Filtrar préstamos por estado: PROCESS")
    void getBorrowByStatus_ProcessStatus_Success() {
        // Given: Préstamos con estado PROCESS
        List<Borrow> borrows = Arrays.asList(testBorrow);
    when(borrowRepository.findByStatus(Status.PENDING)).thenReturn(borrows);

        // When: Filtrar por estado PROCESS
        List<BorrowDTO> result = borrowService.getBorrowByStatus("PROCESS");

    // Then: Retorna solo préstamos en PENDING
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getStatus()).isEqualTo(Status.PENDING);
        
    verify(borrowRepository).findByStatus(Status.PENDING);
    }

    @Test
    @DisplayName("🔍 Filtrar préstamos por estado: BORROWED")
    void getBorrowByStatus_BorrowedStatus_Success() {
    // Given: Préstamos con estado LOANED
    testBorrow.setStatus(Status.BORROWED);
        List<Borrow> borrows = Arrays.asList(testBorrow);
    when(borrowRepository.findByStatus(Status.BORROWED)).thenReturn(borrows);

        // When: Filtrar por estado BORROWED
    List<BorrowDTO> result = borrowService.getBorrowByStatus("LOANED");

    // Then: Retorna solo préstamos prestados
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getStatus()).isEqualTo(Status.BORROWED);
        
    verify(borrowRepository).findByStatus(Status.BORROWED);
    }

    @Test
    @DisplayName("⚠️ Error: Estado inválido en filtro")
    void getBorrowByStatus_InvalidStatus_ThrowsException() {
        // When & Then: Estado inválido debe lanzar excepción
        assertThatThrownBy(() -> borrowService.getBorrowByStatus("INVALID_STATUS"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Tipo de estado inválido");
    }

    @Test
    @DisplayName("Filtrar préstamos por rango de fechas")
    void getBorrowByDate_Success() {
        // Given: Rango de fechas válido
        Date startDate = new Date(System.currentTimeMillis() - 86400000); // Ayer
        Date endDate = new Date(); // Hoy
        List<Borrow> borrows = Arrays.asList(testBorrow);
        
        when(borrowRepository.findByDateBetween(startDate, endDate)).thenReturn(borrows);

        // When: Filtrar por rango de fechas
        List<BorrowDTO> result = borrowService.getBorrowByDate(startDate, endDate);

        // Then: Retorna préstamos en el rango especificado
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1);
        
        verify(borrowRepository).findByDateBetween(startDate, endDate);
    }

    @Test
    @DisplayName("📅 Filtro de fechas sin resultados")
    void getBorrowByDate_NoResults_EmptyList() {
        // Given: Rango de fechas sin préstamos
        Date startDate = new Date(System.currentTimeMillis() - 172800000); // Hace 2 días
        Date endDate = new Date(System.currentTimeMillis() - 86400000); // Ayer
        
        when(borrowRepository.findByDateBetween(startDate, endDate)).thenReturn(Arrays.asList());

        // When: Filtrar por rango sin resultados
        List<BorrowDTO> result = borrowService.getBorrowByDate(startDate, endDate);

        // Then: Retorna lista vacía
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    // 🧪 === TESTS DE VALIDACIONES DE NEGOCIO ===

    @Test
    @DisplayName("✅ Validar todos los estados válidos")
    void getBorrowByStatus_AllValidStatuses_Success() {
        // Given: Lista de préstamos para cada estado
        List<Borrow> borrows = Arrays.asList(testBorrow);
        
        // Test para cada estado válido del enum Status
    String[] validStatuses = {"PROCESS", "REJECTED", "LOANED", "RETURNED"};
    Status[] expectedStatuses = {Status.PENDING, Status.REJECTED, Status.BORROWED, Status.RETURNED};
        
        for (int i = 0; i < validStatuses.length; i++) {
            when(borrowRepository.findByStatus(expectedStatuses[i])).thenReturn(borrows);
            
            // When: Buscar por cada estado válido
            List<BorrowDTO> result = borrowService.getBorrowByStatus(validStatuses[i]);
            
            // Then: Debe funcionar para todos los estados
            assertThat(result).isNotNull();
            verify(borrowRepository).findByStatus(expectedStatuses[i]);
        }
    }

    @Test
    @DisplayName("🔢 Validar cálculo correcto de montos")
    void borrowAmountCalculation_Success() {
        // Given: Múltiples detalles con diferentes precios
        Materials material2 = new Materials();
        material2.setId(20);
        material2.setName("Raspberry Pi 4");
        material2.setPrice(75.00);
        material2.setBorrowable_stock(5);

        DetailsBorrow detail2 = new DetailsBorrow();
        detail2.setId(2);
        detail2.setQuantity(1); // 1 Raspberry Pi
        detail2.setUnitPrice(75.00);
        detail2.setTotalPrice(75.00);
        detail2.setMaterials(material2);
        detail2.setBorrow(testBorrow);

        // Agregar segundo detalle al préstamo
        testBorrow.getDetails().add(detail2);
        testBorrow.setAmount(152.97); // 77.97 + 75.00 = 152.97

        when(borrowRepository.findAll()).thenReturn(Arrays.asList(testBorrow));

        // When: Obtener préstamos
        List<BorrowDTO> result = borrowService.getAllBorrowDTO();

        // Then: Verificar que el monto total es correcto
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAmount()).isEqualTo(152.97);
        assertThat(result.get(0).getDetails()).hasSize(2);
        
        // Verificar detalles individuales
        double totalCalculated = result.get(0).getDetails().stream()
            .mapToDouble(DetailsBorrowDTO::getTotalPrice)
            .sum();
        assertThat(totalCalculated).isEqualTo(152.97);
    }

    @Test
    @DisplayName("📊 Estadísticas de préstamos por estado")
    void borrowStatisticsByStatus_Success() {
        // Given: Préstamos en diferentes estados
        Borrow borrowProcess = new Borrow();
    borrowProcess.setStatus(Status.PENDING);
        borrowProcess.setId(101);
    borrowProcess.setDetails(new ArrayList<>());
        
        Borrow borrowBorrowed = new Borrow();
    borrowBorrowed.setStatus(Status.BORROWED);
        borrowBorrowed.setId(102);
    borrowBorrowed.setDetails(new ArrayList<>());
        
        Borrow borrowReturned = new Borrow();
        borrowReturned.setStatus(Status.RETURNED);
        borrowReturned.setId(103);
    borrowReturned.setDetails(new ArrayList<>());

        // When: Consultar cada estado
    when(borrowRepository.findByStatus(Status.PENDING)).thenReturn(Arrays.asList(borrowProcess));
    when(borrowRepository.findByStatus(Status.BORROWED)).thenReturn(Arrays.asList(borrowBorrowed));
        when(borrowRepository.findByStatus(Status.RETURNED)).thenReturn(Arrays.asList(borrowReturned));
        when(borrowRepository.findByStatus(Status.REJECTED)).thenReturn(Arrays.asList());

        // Then: Verificar conteos por estado
        assertThat(borrowService.getBorrowByStatus("PROCESS")).hasSize(1);
    assertThat(borrowService.getBorrowByStatus("LOANED")).hasSize(1);
        assertThat(borrowService.getBorrowByStatus("RETURNED")).hasSize(1);
        assertThat(borrowService.getBorrowByStatus("REJECTED")).isEmpty();
    }
}
