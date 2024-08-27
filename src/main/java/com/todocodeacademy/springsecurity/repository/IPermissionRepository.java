package com.todocodeacademy.springsecurity.repository;

import com.todocodeacademy.springsecurity.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;



public interface IPermissionRepository extends JpaRepository<Permission,Long> {


}
