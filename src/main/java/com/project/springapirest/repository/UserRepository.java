package com.project.springapirest.repository;

import com.project.springapirest.model.Role;
import com.project.springapirest.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> getAllByRole(Role role);

    Page<User> findAllByRole(Role role, Pageable pageable);
    Page<User> findAllByRoleAndEnabled(Role role, Boolean enabled, Pageable pageable);
    Page<User> findAllByRoleOrRole(Role admin, Role seller, Pageable pageable);

    User findByEmail(String email);

    User findByPhoto(String photoName);
}
