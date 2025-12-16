package com.example.demobase.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PlayerDTO {
    private Long id;
    private String nombre;
    private LocalDate fecha;
}


