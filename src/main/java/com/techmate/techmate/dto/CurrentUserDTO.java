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
    
    @JsonProperty("userName")
    private String user_name;
    
    @JsonProperty("firstName")
    private String first_name;
    
    @JsonProperty("lastName")
    private String last_name;
    
    private String email;
    private List<String> roles;
}

