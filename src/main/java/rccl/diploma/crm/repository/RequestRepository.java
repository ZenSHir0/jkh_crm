package rccl.diploma.crm.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rccl.diploma.crm.entity.Request;
import rccl.diploma.crm.entity.User;
import rccl.diploma.crm.entity.enums.RequestStatus;
import rccl.diploma.crm.entity.enums.RequestType;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByResident(User resident);
    List<Request> findByResidentId(Long residentId);

    List<Request> findByMaster(User master);
    List<Request> findByMasterId(Long masterId);

    List<Request> findByStatus(RequestStatus status);

    List<Request> findByType(RequestType type);

    @Override
    default List<Request> findAll(){
        return findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
