package com.techmate.techmate.infrastructure.service.borrow.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.techmate.techmate.domain.entity.Status;
import com.techmate.techmate.infrastructure.exception.BusinessException;
import com.techmate.techmate.infrastructure.service.borrow.validator.BorrowStateValidator;

class BorrowStateValidatorTest {

    private final BorrowStateValidator validator = new BorrowStateValidator();

    @Test
    void validTransitions_doNotThrow() {
        validator.validateStateTransition(Status.PENDING, Status.BORROWED);
        validator.validateStateTransition(Status.PENDING, Status.REJECTED);
        validator.validateStateTransition(Status.BORROWED, Status.RETURNED);
    }

    @Test
    void invalidTransition_throws() {
        assertThatThrownBy(() -> validator.validateStateTransition(Status.RETURNED, Status.BORROWED))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
