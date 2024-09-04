package com.todocodeacademy.springsecurity.dto;

import jakarta.validation.constraints.NotBlank;

//clse tipo record, pone funciones basicas para agegar contructor gettet y setters
public record AuthLoginRequestDTO (@NotBlank String username, @NotBlank String password)  {


}
