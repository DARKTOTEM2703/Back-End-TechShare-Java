package com.techmate.techmate.service.movements.manager;

import org.springframework.stereotype.Component;

import com.techmate.techmate.entity.Materials;
import com.techmate.techmate.entity.Movements;

@Component
public class MovementStockManager {

    public void adjustMaterialStock(Materials materials, Movements movements) {
        switch (movements.getMoveType()) {
            case STOCK_ADD:
                materials.setBorrowable_stock(materials.getBorrowable_stock() + movements.getQuantity());
                materials.setStock(materials.getStock() + movements.getQuantity());
                break;
            case RETURN:
                // En devoluciones solo restauramos stock prestable y stock
                materials.setBorrowable_stock(materials.getBorrowable_stock() + movements.getQuantity());
                materials.setStock(materials.getStock() + movements.getQuantity());
                break;
            case BORROW:
                if (materials.getStock() < movements.getQuantity()) {
                    throw new IllegalArgumentException("Stock insuficiente para el material");
                }
                materials.setBorrowable_stock(materials.getBorrowable_stock() - movements.getQuantity());
                materials.setStock(materials.getStock() - movements.getQuantity());
                break;
            case ADJUSTMENT:
                int difference = movements.getQuantity() - materials.getStock();
                materials.setBorrowable_stock(materials.getBorrowable_stock() + difference);
                materials.setStock(movements.getQuantity());
                break;
            default:
                throw new IllegalArgumentException("Tipo de movimiento inválido");
        }
    }
}


