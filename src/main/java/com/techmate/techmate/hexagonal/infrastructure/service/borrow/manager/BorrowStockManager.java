package com.techmate.techmate.hexagonal.infrastructure.service.borrow.manager;

import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.hexagonal.domain.entity.Borrow;
import com.techmate.techmate.hexagonal.domain.entity.Materials;
import com.techmate.techmate.hexagonal.domain.entity.MoveType;
import com.techmate.techmate.hexagonal.domain.entity.Movements;
import com.techmate.techmate.hexagonal.domain.entity.Usuario;
import com.techmate.techmate.hexagonal.infrastructure.exception.BorrowBusinessException;
import com.techmate.techmate.hexagonal.domain.repository.MaterialsRepository;
import com.techmate.techmate.hexagonal.domain.repository.MovementsRepository;

/**
 * Gestor de stock especializado para operaciones de préstamo siguiendo SRP.
 * 
 * PRINCIPIOS SOLID APLICADOS:
 * - SRP: Solo se encarga de la gestión de stock para préstamos
 * - OCP: Extensible para nuevas reglas de stock sin modificar código existente
 * - LSP: Puede ser sustituido por cualquier implementación del contrato
 * - ISP: Interfaz específica para gestión de stock de préstamos
 * - DIP: Depende de abstracciones (Repository) no de implementaciones concretas
 * 
 * RESPONSABILIDADES:
 * - Validar disponibilidad de stock para préstamos
 * - Reducir stock cuando se aprueba un préstamo
 * - Restaurar stock cuando se devuelve un préstamo
 * - Manejar transacciones de stock de forma atómica
 * 
 * @author TechShare Team - SOLID Implementation
 */
@Component
public class BorrowStockManager implements IBorrowStockManager {

    private final MaterialsRepository materialsRepository;
    private final MovementsRepository movementsRepository;

    /**
     * Constructor injection para cumplir con DIP.
     * 
     * @param materialsRepository Repositorio de materiales
     * @param movementsRepository Repositorio de movimientos
     */
    public BorrowStockManager(MaterialsRepository materialsRepository, MovementsRepository movementsRepository) {
        this.materialsRepository = materialsRepository;
        this.movementsRepository = movementsRepository;
    }

    /**
     * Valida si hay suficiente stock disponible para préstamo.
     * 
     * @param materialId        ID del material
     * @param requestedQuantity Cantidad solicitada
     * @return true si hay suficiente stock
     * @throws RuntimeException si no hay stock suficiente o material no encontrado
     */
    public boolean validateStockAvailability(Integer materialId, int requestedQuantity) {

        Materials material = materialsRepository.findById(materialId)
                .orElseThrow(() -> BorrowBusinessException.materialNotFound(materialId));

        int availableStock = material.getBorrowableStock();

        if (availableStock < requestedQuantity) {
            throw BorrowBusinessException.insufficientStock(materialId, requestedQuantity, availableStock);
        }

        return true;
    }

    /**
     * Reduce el stock disponible cuando se aprueba un préstamo.
     * Operación transaccional para mantener consistencia.
     * 
     * @param materialId ID del material
     * @param quantity   Cantidad a reducir
     * @throws RuntimeException si no se puede reducir el stock
     */
    @Transactional
    public void reduceStock(Integer materialId, int quantity) {

        Materials material = materialsRepository.findById(materialId)
                .orElseThrow(() -> BorrowBusinessException.materialNotFound(materialId));

        // Validar stock disponible
        if (material.getBorrowableStock() < quantity) {
            throw BorrowBusinessException.insufficientStock(materialId, quantity, material.getBorrowableStock());
        }

        // Reducir stock
        material.setBorrowableStock(material.getBorrowableStock() - quantity);

        // Guardar cambios
        materialsRepository.save(material);
    }

    /**
     * Restaura el stock cuando se devuelve un préstamo.
     * Operación transaccional para mantener consistencia.
     * 
     * @param materialId ID del material
     * @param quantity   Cantidad a restaurar
     * @throws RuntimeException si no se puede restaurar el stock
     */
    @Transactional
    public void restoreStock(Integer materialId, int quantity) {

        Materials material = materialsRepository.findById(materialId)
                .orElseThrow(() -> BorrowBusinessException.materialNotFound(materialId));

        // Restaurar stock
        material.setBorrowableStock(material.getBorrowableStock() + quantity);

        // Guardar cambios
        materialsRepository.save(material);
    }

    /**
     * Obtiene el stock disponible actual para un material.
     * 
     * @param materialId ID del material
     * @return Cantidad de stock disponible
     */
    public int getAvailableStock(Integer materialId) {

        Materials material = materialsRepository.findById(materialId)
                .orElseThrow(() -> BorrowBusinessException.materialNotFound(materialId));

        return material.getBorrowableStock();
    }

    /**
     * Verifica si un material está disponible para préstamos.
     * 
     * @param materialId ID del material
     * @return true si el material está disponible para préstamos
     */
    public boolean isMaterialBorrowable(Integer materialId) {

        Materials material = materialsRepository.findById(materialId)
                .orElseThrow(() -> BorrowBusinessException.materialNotFound(materialId));

        // Un material es prestable si tiene stock > 0
        return material.getBorrowableStock() > 0;
    }

    /**
     * MÉTODO SRP: Reserva stock y registra movimiento de salida (BORROW) de forma
     * atómica.
     * 
     * RESPONSABILIDAD ÚNICA: Gestionar stock + auditoría de movimiento en una sola
     * transacción.
     * 
     * BENEFICIOS:
     * - Testeable: Se puede probar aisladamente con mocks
     * - Reutilizable: Otros servicios pueden usarlo sin duplicar lógica
     * - Mantenible: Cambios en gestión de stock centralizados aquí
     * - Transaccional: Garantiza atomicidad (stock + movimiento juntos)
     * 
     * @param material Material a reservar
     * @param quantity Cantidad a reservar
     * @param borrow   Préstamo asociado
     * @param usuario  Usuario que realiza el préstamo
     * @throws BorrowBusinessException si no hay stock suficiente
     */
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void reserveStockAndLogMovement(Materials material, int quantity, Borrow borrow, Usuario usuario) {
        // Validar parámetros
        if (material == null || borrow == null || usuario == null) {
            throw new IllegalArgumentException("Material, borrow y usuario no pueden ser null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        // 1. Validar stock disponible
        if (material.getBorrowableStock() < quantity) {
            throw BorrowBusinessException.insufficientStock(
                    material.getId(),
                    quantity,
                    material.getBorrowableStock());
        }

        // 2. Decrementar stock disponible
        int newStock = material.getBorrowableStock() - quantity;
        material.setBorrowableStock(newStock);

        // 3. Persistir cambios en Materials materialsRepository.save(material);

        // 4. Crear registro de movimiento (SALIDA/BORROW)
        Movements movement = new Movements();
        movement.setMoveType(MoveType.BORROW);
        movement.setResourceId(material.getId());
        movement.setMaterials(material);
        movement.setUsuario(usuario);
        movement.setMovementDate(new Date());
        movement.setNotes(String.format(
                "Préstamo #%d - Cantidad: %d - Stock restante: %d",
                borrow.getId(),
                quantity,
                newStock));

        // 5. Persistir movimiento
        movementsRepository.save(movement);
    }

    /**
     * MÉTODO SRP: Libera stock y registra movimiento de entrada (RETURN) de forma
     * atómica.
     * 
     * RESPONSABILIDAD ÚNICA: Restaurar stock + auditoría de movimiento en una sola
     * transacción.
     * 
     * BENEFICIOS:
     * - Simetría con reserveStockAndLogMovement() (patrón consistente)
     * - Auditoría completa (trazabilidad de entradas/salidas)
     * - Transaccional: Rollback automático si falla alguna operación
     * - Reutilizable: Devoluciones, ajustes de inventario, etc.
     * 
     * @param material Material a liberar
     * @param quantity Cantidad a liberar
     * @param borrow   Préstamo asociado
     * @param usuario  Usuario que devuelve
     */
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void releaseStockAndLogMovement(Materials material, int quantity, Borrow borrow, Usuario usuario) {
        // Validar parámetros
        if (material == null || borrow == null || usuario == null) {
            throw new IllegalArgumentException("Material, borrow y usuario no pueden ser null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        // 1. Incrementar stock disponible
        int newStock = material.getBorrowableStock() + quantity;
        material.setBorrowableStock(newStock);

        // 2. Persistir cambios en Materials materialsRepository.save(material);

        // 3. Crear registro de movimiento (ENTRADA/RETURN)
        Movements movement = new Movements();
        movement.setMoveType(MoveType.RETURN);
        movement.setResourceId(material.getId());
        movement.setMaterials(material);
        movement.setUsuario(usuario);
        movement.setMovementDate(new Date());
        movement.setNotes(String.format(
                "Devolución préstamo #%d - Cantidad: %d - Stock disponible: %d",
                borrow.getId(),
                quantity,
                newStock));

        // 4. Persistir movimiento
        movementsRepository.save(movement);
    }
}






