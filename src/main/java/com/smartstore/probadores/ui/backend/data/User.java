package com.smartstore.probadores.ui.backend.data;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    private Long id;
    private Boolean active;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean locked = false;
    private String passwordHash;
    private String role;
    private String username;
    private Long idBranch;
}
