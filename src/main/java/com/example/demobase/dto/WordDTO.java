package com.example.demobase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordDTO {
    private Long id;
    private String palabra;
    private boolean utilizada;
}
