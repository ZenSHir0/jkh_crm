package rccl.diploma.crm.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import rccl.diploma.crm.dto.MeterSubmitDTO;
import rccl.diploma.crm.entity.MeterReading;
import rccl.diploma.crm.entity.User;
import rccl.diploma.crm.entity.enums.MeterType;
import rccl.diploma.crm.repository.MeterReadingRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MeterService {

    private final MeterReadingRepository meterReadingRepository;

    public MeterService(MeterReadingRepository meterReadingRepository) {
        this.meterReadingRepository = meterReadingRepository;
    }

    /**
     * Сохраняет показания из формы. Сохраняется по одной записи на каждый тип,
     * у которого введено значение > 0. Повторная передача за тот же период разрешена
     * (перезапись последнего значения).
     */
    @Transactional
    public int submitReadings(MeterSubmitDTO dto, User user, LocalDate period) {
        int saved = 0;

        saved += save(user, period, MeterType.COLD_WATER,  dto.getColdWater());
        saved += save(user, period, MeterType.HOT_WATER,   dto.getHotWater());
        saved += save(user, period, MeterType.GAS,         dto.getGas());
        saved += save(user, period, MeterType.ELECTRICITY, dto.getElectricity());

        if (saved == 0) {
            throw new IllegalArgumentException("Введите показания хотя бы по одному счётчику");
        }
        return saved;
    }

    private int save(User user, LocalDate period, MeterType type, BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) return 0;

        MeterReading reading = MeterReading.builder()
                .user(user)
                .building(user.getBuilding())
                .apartment(user.getApartment())
                .meterType(type)
                .value(value)
                .period(period)
                .submittedAt(LocalDateTime.now())
                .build();

        meterReadingRepository.save(reading);
        return 1;
    }

    /** Вся история показаний жильца (новые первыми). */
    public List<MeterReading> getReadingsForUser(User user) {
        return meterReadingRepository.findByUserOrderByPeriodDescSubmittedAtDesc(user);
    }

    /** Типы счётчиков, уже переданные за текущий период (для пометки в форме). */
    public List<MeterReading> getReadingsForPeriod(User user, LocalDate period) {
        return meterReadingRepository.findByUserAndPeriod(user, period);
    }

    /**
     * Последние показания по каждому типу — для подстановки в поля формы
     * как подсказка (предыдущее значение).
     */
    public Map<MeterType, BigDecimal> getLastValues(User user) {
        Map<MeterType, BigDecimal> result = new EnumMap<>(MeterType.class);
        for (MeterType type : MeterType.values()) {
            Optional<MeterReading> last = meterReadingRepository
                    .findTopByUserAndMeterTypeOrderByPeriodDescSubmittedAtDesc(user, type);
            last.ifPresent(r -> result.put(type, r.getValue()));
        }
        return result;
    }
}
