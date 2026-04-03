package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.UserSanction;
import org.springframework.data.jpa.repository.JpaRepository;
import com.nunclear.escritores.entity.SanctionStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSanctionRepository extends JpaRepository<UserSanction, Integer> {

    List<UserSanction> findByUser_Id(Integer userId);
    List<UserSanction> findByUser_IdAndStatus(Integer userId, SanctionStatus status);
    List<UserSanction> findByAppliedBy_Id(Integer adminId);
}