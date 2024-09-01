package com.todocodeacademy.springsecurity.controller;

import com.todocodeacademy.springsecurity.model.Permission;
import com.todocodeacademy.springsecurity.services.IPermissionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {


    @Autowired
    private IPermissionsService permissionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Permission>> getAllPermissions() {
        List <Permission> permissions = permissionService.findAll();
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity getPermissionById(@PathVariable Long id) {
        Optional<Permission> permission = permissionService.findById(id);
        //con map ddoy formato si esta ok, si no devuelvo not found
        return permission.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Permission> createPermission(@RequestBody Permission permission) {
        Permission newPermission = permissionService.save(permission);
        return ResponseEntity.ok(newPermission);
    }
}
