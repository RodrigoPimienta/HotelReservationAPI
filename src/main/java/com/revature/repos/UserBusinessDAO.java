package com.revature.repos;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBusinessDAO extends JpaRepository<com.revature.models.UserBusiness, Integer> {
}
