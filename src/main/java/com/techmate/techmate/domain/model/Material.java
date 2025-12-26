package com.techmate.techmate.domain.model;

import java.math.BigDecimal;

public class Material {
    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer borrowableStock;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getBorrowableStock() {
        return borrowableStock;
    }

    public void setBorrowableStock(Integer borrowableStock) {
        this.borrowableStock = borrowableStock;
    }
}
