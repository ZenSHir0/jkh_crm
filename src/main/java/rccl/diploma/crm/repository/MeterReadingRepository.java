package rccl.diploma.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rccl.diploma.crm.entity.MeterReading;
import rccl.diploma.crm.entity.User;
import rccl.diploma.crm.entity.enums.MeterType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {

    /** Вся история жильца, новые первыми. */
    List<MeterReading> findByUserOrderByPeriodDescSubmittedAtDesc(User user);

    /** Показания жильца за конкретный период. */
    List<MeterReading> findByUserAndPeriod(User user, LocalDate period);

    /** Последнее показание конкретного счётчика жильца (для подстановки в форму). */
    Optional<MeterReading> findTopByUserAndMeterTypeOrderByPeriodDescSubmittedAtDesc(
            User user, MeterType meterType);

    /** Для администратора — все показания за период. */
    List<MeterReading> findByPeriodOrderByUserAscMeterTypeAsc(LocalDate period);

    /** Для администратора — все показания по конкретному дому. */
    List<MeterReading> findByBuildingIdOrderByPeriodDescSubmittedAtDesc(Long buildingId);
}
