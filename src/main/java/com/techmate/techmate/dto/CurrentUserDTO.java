package com.techmate.techmate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserDTO {
    private Integer id;
    private String user_name;
    private String first_name;
    private String last_name;
    private String email;
    private List<String> roles;
}

