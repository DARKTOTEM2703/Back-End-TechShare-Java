package com.techmate.techmate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserDTO {
    private Integer id;
    
private String userName;

    private String firstName;

    private String lastName;
    
    private String email;
    private List<String> roles;
}

