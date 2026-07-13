package org.example.petprojectweather.repository;

import org.example.petprojectweather.entity.Role;
import org.example.petprojectweather.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role,Long> {
    List<Role> getByUser(User user);
}
