package com.smartstore.probadores.ui.backend.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PriorityEnum {
    HIGH("ALTA"),
    MEDIUM("MEDIA"),
    LOW("BAJA");

    private String value;
}
