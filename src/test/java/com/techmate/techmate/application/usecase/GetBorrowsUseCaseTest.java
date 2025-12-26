package com.techmate.techmate.application.usecase;

import com.techmate.techmate.domain.model.Borrow;
import com.techmate.techmate.domain.model.DetailsBorrow;
import com.techmate.techmate.domain.port.out.BorrowRepositoryPort;
import com.techmate.techmate.domain.port.out.MaterialsRepositoryPort;
import com.techmate.techmate.domain.port.out.UsuarioRepositoryPort;
import com.techmate.techmate.dto.BorrowReadDTO;
import com.techmate.techmate.dto.DetailsBorrowDTO;
import com.techmate.techmate.entity.Materials;
import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.service.borrow.mapper.BorrowDtoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetBorrowsUseCaseTest {

    @Mock
    BorrowRepositoryPort borrowRepositoryPort;

    @Mock
    UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    MaterialsRepositoryPort materialsRepositoryPort;

    @Mock
    BorrowDtoMapper borrowDtoMapper;

    @InjectMocks
    GetBorrowsUseCase getBorrowsUseCase;

    @Test
    void getAll_batchFetchesMaterials_and_enrichesDetails() {
        // arrange: build a borrow with a direct resourceId and a details entry
        // referencing another material
        Borrow b = new Borrow();
        b.setId(1);
        b.setUsuarioId(10);
        b.setAmount(BigDecimal.ZERO);
        b.setDate(null);
        // resourceId method not present in domain Borrow; use reflection if needed or
        // assume getter present
        try {
            java.lang.reflect.Method m = Borrow.class.getMethod("setResourceId", Integer.class);
            m.invoke(b, 100);
        } catch (Exception ignored) {
        }

        DetailsBorrow d = new DetailsBorrow();
        d.setId(5);
        d.setMaterialsId(200);
        d.setQuantity(2);
        d.setUnitPrice(new BigDecimal("10.00"));
        d.setTotalPrice(new BigDecimal("20.00"));
        b.setDetails(List.of(d));

        when(borrowRepositoryPort.findAll()).thenReturn(List.of(b));

        Usuario user = new Usuario();
        user.setId(10);
        user.setUser_name("juan");
        user.setEmail("juan@example.com");
        when(usuarioRepositoryPort.findAllByIds(anyList())).thenReturn(List.of(user));

        com.techmate.techmate.domain.model.Material m100 = new com.techmate.techmate.domain.model.Material();
        m100.setId(100);
        m100.setName("Material-100");
        com.techmate.techmate.domain.model.Material m200 = new com.techmate.techmate.domain.model.Material();
        m200.setId(200);
        m200.setName("Material-200");

        when(materialsRepositoryPort.findAllByIds(anyList())).thenReturn(List.of(m100, m200));

        // Mapper: return a BorrowReadDTO with a details DTO that copies materialsId, id
        // and quantity
        DetailsBorrowDTO detailDto = new DetailsBorrowDTO();
        detailDto.setId(5);
        detailDto.setMaterialsId(200);
        detailDto.setQuantity(2);
        BorrowReadDTO readDto = new BorrowReadDTO();
        readDto.setId(1);
        readDto.setUsuarioId(10);
        readDto.setDetails(List.of(detailDto));

        when(borrowDtoMapper.toReadDto(eq(b), any(Usuario.class),
                any(com.techmate.techmate.domain.model.Material.class))).thenReturn(readDto);

        // act
        List<BorrowReadDTO> result = getBorrowsUseCase.getAll();

        // assert: materialsRepositoryPort should be called with both 100 and 200
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(materialsRepositoryPort, times(1)).findAllByIds(captor.capture());
        List<Integer> requested = captor.getValue();
        assertThat(requested).containsExactlyInAnyOrder(100, 200);

        // assert: returned dto has details enriched with borrowId and materialName
        assertThat(result).hasSize(1);
        BorrowReadDTO out = result.get(0);
        assertThat(out.getDetails()).hasSize(1);
        DetailsBorrowDTO od = out.getDetails().get(0);
        assertThat(od.getBorrowId()).isEqualTo(1);
        assertThat(od.getMaterialName()).isEqualTo("Material-200");
    }
}
