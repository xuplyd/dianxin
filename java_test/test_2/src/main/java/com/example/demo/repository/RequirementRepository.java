package com.example.demo.repository;

import com.example.demo.entity.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RequirementRepository extends JpaRepository<Requirement, String>, JpaSpecificationExecutor<Requirement> {
    // JpaSpecificationExecutor 提供动态查询功能，无需额外方法
}
