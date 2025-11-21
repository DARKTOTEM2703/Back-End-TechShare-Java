package com.techmate.techmate.config;

import com.techmate.techmate.entity.MoveType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.lang.Nullable;

/**
 * Converter to support legacy string values for MoveType request params.
 * Maps legacy values like "OUT" to the corresponding MoveType.
 */
@Component
public class MoveTypeConverter implements Converter<String, MoveType> {

    @Override
    public MoveType convert(@Nullable String source) {
        if (source == null) return null;
        String s = source.trim().toUpperCase();
        // Legacy mappings
        switch (s) {
            case "OUT":
                return MoveType.BORROW;
            case "IN":
                return MoveType.RETURN;
            // allow underscores or dashes and direct enum names
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


