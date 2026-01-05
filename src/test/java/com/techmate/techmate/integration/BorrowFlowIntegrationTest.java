package com.techmate.techmate.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.core.domain.entity.Borrow;
import com.techmate.techmate.core.domain.entity.DetailsBorrow;
import com.techmate.techmate.core.domain.entity.Materials;
import com.techmate.techmate.core.domain.entity.MoveType;
import com.techmate.techmate.core.domain.entity.Status;
import com.techmate.techmate.core.domain.entity.Usuario;
import com.techmate.techmate.infrastructure.persistence.repository.BorrowRepository;
import com.techmate.techmate.infrastructure.persistence.repository.MaterialsRepository;
import com.techmate.techmate.infrastructure.persistence.repository.MovementsRepository;
import com.techmate.techmate.infrastructure.persistence.repository.UsuarioRepository;
import com.techmate.techmate.infrastructure.service.BorrowService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BorrowFlowIntegrationTest {

    @Autowired
    MaterialsRepository materialsRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    BorrowRepository borrowRepository;

    @Autowired
    MovementsRepository movementsRepository;

    @Autowired
    BorrowService borrowService;

    @Test
    void fullBorrowFlow_reduces_and_restores_stock_and_creates_movements() throws Exception {
        // Crear material
        Materials m = new Materials();
        m.setName("IntegrationMat");
        m.setDescription("desc");
        m.setPrice(BigDecimal.TEN);
        m.setStock(10);
        m.setBorrowableStock(10);
        m = materialsRepository.save(m);

        // Crear usuario solicitante y admin
        Usuario user = new Usuario();
        user.setUser_name("user1");
        user.setEmail("u1@example.com");
        user = usuarioRepository.save(user);

        Usuario admin = new Usuario();
        admin.setUser_name("admin");
        admin.setEmail("admin@example.com");
        admin = usuarioRepository.save(admin);

        // Crear borrow con detalle
        Borrow b = new Borrow();
        b.setStatus(Status.PENDING);
        b.setUsuario(user);

        DetailsBorrow d = new DetailsBorrow();
        d.setMaterials(m);
        d.setQuantity(2);
        d.setUnitPrice(BigDecimal.TEN);
        d.setTotalPrice(BigDecimal.valueOf(20));
        d.setBorrow(b);
        b.setDetails(new java.util.ArrayList<>(List.of(d)));

        b = borrowRepository.save(b);

        // Estado inicial: stock no cambiado
        Materials before = materialsRepository.findById(m.getId()).get();
        assertThat(before.getBorrowableStock()).isEqualTo(10);

        // Aprobar préstamo
        borrowService.updateBorrowStatus(b.getId(), Status.BORROWED, admin.getId());

        Materials afterBorrow = materialsRepository.findById(m.getId()).get();
        assertThat(afterBorrow.getBorrowableStock()).isEqualTo(8);

        // Movimientos: debe existir al menos un movimiento de BORROW
        List<com.techmate.techmate.domain.entity.Movements> movements = movementsRepository.findAll();
        boolean hasBorrow = movements.stream().anyMatch(x -> x.getMoveType() == MoveType.BORROW);
        assertThat(hasBorrow).isTrue();

        // Devolver
        borrowService.updateBorrowStatus(b.getId(), Status.RETURNED, admin.getId());

        Materials afterReturn = materialsRepository.findById(m.getId()).get();
        assertThat(afterReturn.getBorrowableStock()).isEqualTo(10);

        boolean hasReturn = movementsRepository.findAll().stream().anyMatch(x -> x.getMoveType() == MoveType.RETURN);
        assertThat(hasReturn).isTrue();
    }
}
