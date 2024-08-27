package com.todocodeacademy.springsecurity.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="roles")
public class Role {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String role;

    @ManyToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
    //tabla intermedia  roles_perssions
    @JoinTable(name="roles_permissions", joinColumns = @JoinColumn(name="role_id"),
    inverseJoinColumns=@JoinColumn(name="permission_id"))
    //se establece la relacion con este objeto
    private Set<Permission> permissionsList=new HashSet<>();

}
