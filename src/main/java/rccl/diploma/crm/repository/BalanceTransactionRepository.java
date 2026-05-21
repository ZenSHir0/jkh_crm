package rccl.diploma.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rccl.diploma.crm.entity.BalanceTransaction;
import rccl.diploma.crm.entity.User;

import java.util.List;

@Repository
public interface BalanceTransactionRepository extends JpaRepository<BalanceTransaction, Long> {
    List<BalanceTransaction> findByMasterOrderByCreatedAtDesc(User master);
}
