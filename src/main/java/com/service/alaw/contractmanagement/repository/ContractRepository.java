package com.service.alaw.contractmanagement.repository;

import com.service.alaw.contractmanagement.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    List<Contract> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(Long userId);

    Optional<Contract> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);

    Optional<Contract> findByIdAndDeletedFalse(Long id);
}
