package com.service.alaw.contractmanagement.repository;

import com.service.alaw.contractmanagement.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    // 사용자의 계약서 목록 조회
    List<Contract> findByMemberIdOrderByCreatedDateDesc(Long memberId);

    // 사용자의 특정 계약서 조회
    Optional<Contract> findByContractIdAndMemberId(Long contractId, Long memberId);

    // 사용자의 즐겨찾기 계약서 목록 조회
    List<Contract> findByMemberIdAndBookmarkTrueOrderByCreatedDateDesc(Long memberId);
}
