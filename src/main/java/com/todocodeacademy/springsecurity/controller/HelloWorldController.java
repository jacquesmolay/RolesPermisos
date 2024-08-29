package com.todocodeacademy.springsecurity.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {



    @GetMapping("/holaseg")
    @PreAuthorize("hasAuthority('READ')")
    public String secHelloWorld() {

        return "Hola Mundo TodoCode con seguridad";
    }

    @GetMapping("/holanoseg")
    @PreAuthorize("permiteAll()")
    public String noSecHelloWorld() {

        return "Hola mundo TodoCode sin seguridad";
    }

}
