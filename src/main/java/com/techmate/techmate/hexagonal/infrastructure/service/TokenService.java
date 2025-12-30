package com.techmate.techmate.hexagonal.infrastructure.service;
import java.util.*;

public interface TokenService {
    Integer getUserIdFromToken(String token);

    Optional<List<Integer>> getRolesFromToken(String token);

    String getUserEmailFromToken(String token);

    String getUserNameFromToken(String token);
    
}




