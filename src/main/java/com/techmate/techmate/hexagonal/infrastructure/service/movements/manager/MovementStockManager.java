package com.techmate.techmate.hexagonal.infrastructure.service.movements.manager;

import org.springframework.stereotype.Component;

import com.techmate.techmate.hexagonal.domain.entity.Materials;
import com.techmate.techmate.hexagonal.domain.entity.Movements;

@Component
public class MovementStockManager {

    public void adjustMaterialStock(Materials materials, Movements movements) {
        switch (movements.getMoveType()) {
            case STOCK_ADD:
                materials.setBorrowableStock(materials.getBorrowableStock() + movements.getQuantity());
                materials.setStock(materials.getStock() + movements.getQuantity());
                break;
            case RETURN:
                // En devoluciones solo restauramos stock prestable y stock
                materials.setBorrowableStock(materials.getBorrowableStock() + movements.getQuantity());
                materials.setStock(materials.getStock() + movements.getQuantity());
                break;
            case BORROW:
                if (materials.getStock() < movements.getQuantity()) {
                    throw new IllegalArgumentException("Stock insuficiente para el material");
                }
                materials.setBorrowableStock(materials.getBorrowableStock() - movements.getQuantity());
                materials.setStock(materials.getStock() - movements.getQuantity());
                break;
            case ADJUSTMENT:
                int difference = movements.getQuantity() - materials.getStock();
                materials.setBorrowableStock(materials.getBorrowableStock() + difference);
                materials.setStock(movements.getQuantity());
                break;
            default:
                throw new IllegalArgumentException("Tipo de movimiento inválido");
        }
    }
}






