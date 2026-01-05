package com.techmate.techmate.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MoveType {
    BORROW,
    RETURN,
    STOCK_ADD,
    ADJUSTMENT;

    @JsonValue
    public String toJson() {
        // Use legacy short codes for external JSON API
        switch (this) {
            case BORROW:
                return "OUT";
            case RETURN:
                return "IN";
            default:
                return this.name();
        }
    }

    @JsonCreator
    public static MoveType fromJson(String value) {
        if (value == null) return null;
        String s = value.trim().toUpperCase();
        switch (s) {
            case "OUT":
                return BORROW;
            case "IN":
                return RETURN;
            default:
                s = s.replace('-', '_');
                s = s.replace(' ', '_');
                try {
                    return MoveType.valueOf(s);
                } catch (IllegalArgumentException ex) {
                    return null;
                }
        }
    }
}










