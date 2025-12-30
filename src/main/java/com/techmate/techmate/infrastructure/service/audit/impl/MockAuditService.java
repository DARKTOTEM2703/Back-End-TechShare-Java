package com.techmate.techmate.infrastructure.service.audit.impl;

import com.techmate.techmate.infrastructure.service.audit.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementación mock del servicio de auditoría.
 * En producción, se registraría en base de datos dedicada o sistema externo.
 */
@Slf4j
@Service
public class MockAuditService implements AuditService {
    
    @Override
    public void logAudit(String action, String entity, Integer entityId, String details, Integer userId) {
        log.info(
            "📋 [AUDIT] Acción: {}, Entidad: {}, ID: {}, Usuario: {}, Detalles: {}",
            action, entity, entityId, userId, details
        );
    }
    
    @Override
    public void logBorrow(Integer borrowId, String action, Integer userId, String details) {
        logAudit(action, "BORROW", borrowId, details, userId);
    }
    
    @Override
    public void logMaterial(Integer materialId, String action, String details) {
        logAudit(action, "MATERIAL", materialId, details, null);
    }
}








