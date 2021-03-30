package com.project.springapirest.repository;

import com.project.springapirest.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("from Role")
    List<Role> findAllRoles();
}
