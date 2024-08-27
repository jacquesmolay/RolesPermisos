package com.todocodeacademy.springsecurity.repository;

import com.todocodeacademy.springsecurity.model.UserSec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<UserSec, Long>  {

    //Crea la sentencia en base al nombre en inglés del método
    //Tmb se puede hacer mediante Query pero en este caso no es necesario por ser el mismo base de datos, pero al jugar con el nombre se spring puede identidicar que queremos traer un nombre
    Optional<UserSec> findUserEntityByUsername(String username);

}
