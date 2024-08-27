package com.todocodeacademy.springsecurity.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSec {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id; //nombre de usuario unico
    @Column(unique=true)
    private String username;
    private String password;

    //campos solictados por spring security, los detecta automaticamente
    private boolean enabled; //cuenta habilitada no ha sido borrada fisicamente, solo logicamente
    private boolean accountNotExpired; //cuenta exprirada?
    private boolean accountNotLocked; //cuenta no bloqueada
    private boolean credentialNotExpired; //cotraseña no ha expirado

    @ManyToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
    @JoinTable(name="user_roles", joinColumns = @JoinColumn(name="user_id"),
    inverseJoinColumns=@JoinColumn(name="role_id"))

    //se establece la relacion con este objeto
    private Set<Role> rolesList=new HashSet<>();



}
