package rccl.diploma.crm.entity;

import jakarta.persistence.*;
import lombok.*;
import rccl.diploma.crm.entity.enums.MeterType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "meter_readings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    private Building building;

    /** Номер квартиры на момент передачи (копируется из профиля). */
    @Column
    private String apartment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeterType meterType;

    /** Показание счётчика. */
    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal value;

    /**
     * Расчётный период — первый день месяца, за который передаются показания.
     * Например: 2026-05-01 означает «Май 2026».
     */
    @Column(nullable = false)
    private LocalDate period;

    @Column(nullable = false)
    private LocalDateTime submittedAt;
}
