package com.smartstore.probadores.ui.backend.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StateEnum {
    PENDING("Pendiente"),
    RESOLVED("Resuelto");

    private String value;
}
