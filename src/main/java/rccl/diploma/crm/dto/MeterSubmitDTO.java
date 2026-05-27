package rccl.diploma.crm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class MeterSubmitDTO {

    private BigDecimal coldWater;
    private BigDecimal hotWater;
    private BigDecimal gas;
    private BigDecimal electricity;
}
