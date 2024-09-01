package com.todocodeacademy.springsecurity.controller;

import com.todocodeacademy.springsecurity.model.Permission;
import com.todocodeacademy.springsecurity.model.Role;
import com.todocodeacademy.springsecurity.services.IPermissionsService;
import com.todocodeacademy.springsecurity.services.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private IRoleService roleService;

        //en este caso rol se comunica con permisos por lo que hago un autowired a permisos
    @Autowired
    private IPermissionsService permissionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Role>> getAllRoles() {
        List roles = roleService.findAll();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        Optional<Role> role = roleService.findById(id);
        return role.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity createRole(@RequestBody Role role) {
        Set<Permission> permissionList = new HashSet<Permission>();
        Permission readPermission;

        // Recuperar la Permission/s por su ID
        for (Permission per : role.getPermissionsList()) {
            readPermission = permissionService.findById(per.getId()).orElse(null);
            if (readPermission != null) {
                //si encuentro, guardo en la lista
                permissionList.add(readPermission);
            }
        }

        role.setPermissionsList(permissionList);
        Role newRole = roleService.save(role);
        return ResponseEntity.ok(newRole);
    }

    @PatchMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editRole(@PathVariable Long id, @RequestBody Role editRole){

        //busco rol y lo guardo en una clase

        ResponseEntity<Role> roleBuscar=this.getRoleById(id);
        roleBuscar.getBody().setRole(editRole.getRole());
        roleBuscar.getBody().setPermissionsList(editRole.getPermissionsList());

        Role rolGrabar=new Role();
        rolGrabar.setId(roleBuscar.getBody().getId());
        rolGrabar.setRole(roleBuscar.getBody().getRole());
        rolGrabar.setPermissionsList(roleBuscar.getBody().getPermissionsList());


        this.createRole(rolGrabar);

        return "elemento atualizado";



    }


}
