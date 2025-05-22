package com.example.tj.DisasterManagementPortal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.tj.DisasterManagementPortal.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
