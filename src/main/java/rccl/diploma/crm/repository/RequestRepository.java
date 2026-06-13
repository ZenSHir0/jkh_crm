package rccl.diploma.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import rccl.diploma.crm.entity.Request;
import rccl.diploma.crm.entity.User;
import rccl.diploma.crm.entity.enums.RequestStatus;
import rccl.diploma.crm.entity.enums.RequestType;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long>, JpaSpecificationExecutor<Request> {

    Page<Request> findByStatus(RequestStatus status, Pageable pageable);
    Page<Request> findByType(RequestType type, Pageable pageable);
    Page<Request> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Request> findByResident(User resident, Pageable pageable);
    List<Request> findTop10ByResidentOrderByCreatedAtDesc(User resident);
    Page<Request> findByStatusAndType(RequestStatus status, RequestType type, Pageable pageable);
    Page<Request> findByMaster(User master, Pageable pageable);
    long countByStatus(RequestStatus status);

}
