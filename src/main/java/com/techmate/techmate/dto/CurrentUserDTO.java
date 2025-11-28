package com.techmate.techmate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Setter
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

