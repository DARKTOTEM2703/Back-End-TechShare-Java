package com.techmate.techmate.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.techmate.techmate.hexagonal.domain.entity.Borrow;

import java.util.List;

/**
 * Test de integración para detectar N+1 usando Hibernate Statistics.
 * Ejecuta `borrowRepository.findAll()` y comprueba el número de sentencias
 * preparadas ejecutadas. Si el repositorio usa JOIN FETCH correctamente,
 * el número de sentencias debe ser bajo (1 consulta para recuperar borrows
 * con relaciones cargadas).
 */
@SpringBootTest
@ActiveProfiles("test")
public class BorrowNPlusOneIntegrationTest {

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private EntityManagerFactory emf;

    @Test
    public void detectNoNPlusOneOnFindAll() {
        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        Statistics stats = sessionFactory.getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();

        List<Borrow> all = borrowRepository.findAll();
        // asegurar que hay datos o al menos que la llamada funcionó
        assertThat(all).isNotNull();

        long prepares = stats.getPrepareStatementCount();
        System.out.println("Hibernate prepareStatementCount=" + prepares);

        // Umbral conservador: permitimos pequeñas consultas extra (ej. 1-3),
        // pero rechazamos patrones N+1 masivos. Ajusta según tu contexto.
        assertThat(prepares).isLessThanOrEqualTo(5);
    }
}
