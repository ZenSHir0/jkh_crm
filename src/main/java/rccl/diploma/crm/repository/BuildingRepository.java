package rccl.diploma.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rccl.diploma.crm.entity.Building;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
}
