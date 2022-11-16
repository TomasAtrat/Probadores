package com.smartstore.probadores.ui.backend.data.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Task {
    private Long id;
    private String title;
    private String description;
    private String priority;
    private String category;
    private String state;
    private User userId;
}
