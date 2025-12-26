package com.techmate.techmate.application.usecase;

import com.techmate.techmate.domain.model.Borrow;
import com.techmate.techmate.domain.model.DetailsBorrow;
import com.techmate.techmate.domain.model.Material;
import com.techmate.techmate.domain.port.out.BorrowRepositoryPort;
import com.techmate.techmate.domain.port.out.DetailsBorrowRepositoryPort;
import com.techmate.techmate.domain.port.out.MaterialsRepositoryPort;
import com.techmate.techmate.dto.BorrowDTO;
import com.techmate.techmate.dto.DetailsBorrowDTO;
import com.techmate.techmate.entity.Status;
import com.techmate.techmate.service.borrow.mapper.BorrowMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CreateBorrowUseCase (Onion Architecture).
 * Tests domain logic: borrowing materials, stock validation, role validation.
 */
@ExtendWith(MockitoExtension.class)
class CreateBorrowUseCaseTest {

    @Mock
    BorrowRepositoryPort borrowRepositoryPort;

    @Mock
    MaterialsRepositoryPort materialsRepositoryPort;

    @Mock
    DetailsBorrowRepositoryPort detailsBorrowRepositoryPort;

    @Mock
    BorrowMapper borrowMapper;

    @InjectMocks
    CreateBorrowUseCase createBorrowUseCase;

    @Test
    void execute_withValidData_createsBorrowAndReducesStock() throws Exception {
        // Arrange
        BorrowDTO dto = new BorrowDTO();
        dto.setUsuarioId(1);
        dto.setDate(new java.util.Date());

        DetailsBorrowDTO detailDto = new DetailsBorrowDTO();
        detailDto.setId(100);
        detailDto.setQuantity(5);
        dto.setDetails(List.of(detailDto));

        Material mat = new Material();
        mat.setId(100);
        mat.setName("Test Material");
        mat.setPrice(new BigDecimal("10.00"));
        mat.setBorrowableStock(10);

        Borrow savedBorrow = new Borrow();
        savedBorrow.setId(1);
        savedBorrow.setUsuarioId(1);
        savedBorrow.setStatus(Status.PENDING);
        savedBorrow.setAmount(new BigDecimal("50.00"));

        when(materialsRepositoryPort.findById(100)).thenReturn(Optional.of(mat));
        when(borrowRepositoryPort.save(any(Borrow.class))).thenReturn(savedBorrow);
        when(detailsBorrowRepositoryPort.save(any(DetailsBorrow.class))).thenReturn(new DetailsBorrow());

        com.techmate.techmate.entity.Borrow jpaBorrow = new com.techmate.techmate.entity.Borrow();
        jpaBorrow.setId(1);
        when(borrowMapper.toDTO(any())).thenReturn(new BorrowDTO());

        List<Integer> roles = List.of(1, 2);

        // Act
        BorrowDTO result = createBorrowUseCase.execute(dto, roles);

        // Assert
        assertThat(result).isNotNull();

        // Verify stock was reduced
        ArgumentCaptor<Material> captor = ArgumentCaptor.forClass(Material.class);
        verify(materialsRepositoryPort, times(1)).save(captor.capture());
        Material savedMat = captor.getValue();
        assertThat(savedMat.getBorrowableStock()).isEqualTo(5); // 10 - 5
    }

    @Test
    void execute_withoutRoles_throwsIllegalArgumentException() {
        // Arrange
        BorrowDTO dto = new BorrowDTO();
        dto.setDetails(List.of());

        // Act & Assert
        assertThatThrownBy(() -> createBorrowUseCase.execute(dto, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("USER_NO_ROLES");

        verify(borrowRepositoryPort, never()).save(any());
    }

    @Test
    void execute_withInsufficientStock_throwsException() {
        // Arrange
        BorrowDTO dto = new BorrowDTO();
        dto.setUsuarioId(1);
        dto.setDate(new java.util.Date());

        DetailsBorrowDTO detailDto = new DetailsBorrowDTO();
        detailDto.setId(100);
        detailDto.setQuantity(20); // More than available
        dto.setDetails(List.of(detailDto));

        Material mat = new Material();
        mat.setId(100);
        mat.setName("Low Stock Material");
        mat.setPrice(new BigDecimal("10.00"));
        mat.setBorrowableStock(5); // Only 5 available

        when(materialsRepositoryPort.findById(100)).thenReturn(Optional.of(mat));
        when(borrowRepositoryPort.save(any(Borrow.class))).thenReturn(new Borrow());

        List<Integer> roles = List.of(1);

        // Act & Assert
        assertThatThrownBy(() -> createBorrowUseCase.execute(dto, roles))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("BORROW_INSUFFICIENT_STOCK");
    }

    @Test
    void execute_withMaterialNotFound_throwsException() {
        // Arrange
        BorrowDTO dto = new BorrowDTO();
        dto.setUsuarioId(1);
        dto.setDate(new java.util.Date());

        DetailsBorrowDTO detailDto = new DetailsBorrowDTO();
        detailDto.setId(999); // Non-existent material
        detailDto.setQuantity(1);
        dto.setDetails(List.of(detailDto));

        when(materialsRepositoryPort.findById(999)).thenReturn(Optional.empty());
        when(borrowRepositoryPort.save(any(Borrow.class))).thenReturn(new Borrow());

        List<Integer> roles = List.of(1);

        // Act & Assert
        assertThatThrownBy(() -> createBorrowUseCase.execute(dto, roles))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("MATERIAL_NOT_FOUND");
    }

    @Test
    void execute_calculatesTotalAmountCorrectly() throws Exception {
        // Arrange
        BorrowDTO dto = new BorrowDTO();
        dto.setUsuarioId(1);
        dto.setDate(new java.util.Date());

        DetailsBorrowDTO detail1 = new DetailsBorrowDTO();
        detail1.setId(100);
        detail1.setQuantity(2);

        DetailsBorrowDTO detail2 = new DetailsBorrowDTO();
        detail2.setId(200);
        detail2.setQuantity(3);

        dto.setDetails(List.of(detail1, detail2));

        Material mat1 = new Material();
        mat1.setId(100);
        mat1.setPrice(new BigDecimal("10.00"));
        mat1.setBorrowableStock(10);

        Material mat2 = new Material();
        mat2.setId(200);
        mat2.setPrice(new BigDecimal("15.00"));
        mat2.setBorrowableStock(10);

        Borrow savedBorrow = new Borrow();
        savedBorrow.setId(1);
        savedBorrow.setAmount(new BigDecimal("65.00")); // (10*2) + (15*3)

        when(materialsRepositoryPort.findById(100)).thenReturn(Optional.of(mat1));
        when(materialsRepositoryPort.findById(200)).thenReturn(Optional.of(mat2));
        when(borrowRepositoryPort.save(any(Borrow.class))).thenReturn(savedBorrow);
        when(detailsBorrowRepositoryPort.save(any(DetailsBorrow.class))).thenReturn(new DetailsBorrow());

        com.techmate.techmate.entity.Borrow jpaBorrow = new com.techmate.techmate.entity.Borrow();
        jpaBorrow.setId(1);
        when(borrowMapper.toDTO(any())).thenReturn(new BorrowDTO());

        // Act
        createBorrowUseCase.execute(dto, List.of(1));

        // Assert
        ArgumentCaptor<Borrow> captor = ArgumentCaptor.forClass(Borrow.class);
        verify(borrowRepositoryPort, atLeastOnce()).save(captor.capture());
        Borrow finalBorrow = captor.getValue();
        assertThat(finalBorrow.getAmount()).isEqualTo(new BigDecimal("65.00"));
    }
}
