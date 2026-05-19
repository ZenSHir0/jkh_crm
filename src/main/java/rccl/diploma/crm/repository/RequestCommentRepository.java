package rccl.diploma.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rccl.diploma.crm.entity.RequestComment;

@Repository
public interface RequestCommentRepository extends JpaRepository<RequestComment, Long> {
}
