package com.todocodeacademy.springsecurity.services;
import com.todocodeacademy.springsecurity.model.Permission;
import java.util.List;
import java.util.Optional;

public interface IPermissionsService {

    List <Permission>findAll();
    Optional<Permission> findById(Long id); //Optional es un conteneor que puede tenero o no un valor
    Permission save(Permission permission);
    void deleteById(Long id);
    Permission update(Permission permission);
}
