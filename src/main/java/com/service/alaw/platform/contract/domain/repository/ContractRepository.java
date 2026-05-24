package com.service.alaw.platform.contract.domain.repository;

import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

  // 사용자의 계약서 목록 조회
  List<Contract> findByUserOrderByCreatedDateDesc(User user);

  // 사용자의 특정 계약서 조회
  Optional<Contract> findByContractIdAndUser(Long contractId, User user);

  // 사용자의 즐겨찾기 계약서 목록 조회
  List<Contract> findByUserAndBookmarkTrueOrderByCreatedDateDesc(User user);

  // userId로 조회 (Service에서 User 객체 없이 조회할 때 사용)
  List<Contract> findByUser_UserIdOrderByCreatedDateDesc(Long userId);

  // 사용자가 저장 확정한 계약서 목록 조회
  List<Contract> findByUser_UserIdAndUserSavedTrueOrderByCreatedDateDesc(Long userId);

  Optional<Contract> findByContractIdAndUser_UserId(Long contractId, Long userId);

  boolean existsByUser_UserIdAndTitleIgnoreCaseAndUserSavedTrue(Long userId, String title);

  boolean existsByUser_UserIdAndTitleIgnoreCaseAndUserSavedTrueAndContractIdNot(
      Long userId, String title, Long contractId);
}
