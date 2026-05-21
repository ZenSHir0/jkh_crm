package rccl.diploma.crm.entity.enums;

import java.math.BigDecimal;

public enum RequestType {
    HEATING_ISSUES("Отсутствие/некачественное отопление",   new BigDecimal("900")),
    HOT_WATER_ISSUES("Проблемы с горячей водой",            new BigDecimal("700")),
    COLD_WATER_ISSUES("Проблемы с холодной водой",          new BigDecimal("700")),
    SEWERAGE_ISSUES("Засор/авария канализации",             new BigDecimal("1200")),
    ELEVATOR_ISSUES("Неисправность лифта",                  new BigDecimal("2000")),
    LIGHTING_ISSUES("Неисправность освещения МОП",          new BigDecimal("600")),
    ROOF_LEAKAGE("Протечка кровли / водостока",             new BigDecimal("1500")),
    VENTILATION_ISSUES("Проблемы с вентиляцией",            new BigDecimal("800")),
    GAS_EQUIPMENT("Неисправность газового оборудования",    new BigDecimal("1800")),
    BUILDING_STRUCTURE("Повреждение конструкций дома",      new BigDecimal("1600")),
    ELECTRICITY_ISSUES("Проблемы с электроснабжением",      new BigDecimal("900")),
    COMMON_AREA_MAINTENANCE("Ненадлежащее содержание МОП",  new BigDecimal("500")),
    BILLING_ERRORS("Перерасчёт / ошибки в ЕПД",            new BigDecimal("0")),
    MANAGEMENT_COMPLAINTS("Разъяснения и жалобы на УК/ТСЖ", new BigDecimal("0")),
    OTHER("Другое",                                         new BigDecimal("500"));

    private final String displayName;
    private final BigDecimal price;

    RequestType(String displayName, BigDecimal price) {
        this.displayName = displayName;
        this.price = price;
    }

    public String getDisplayName() { return displayName; }
    public BigDecimal getPrice()   { return price; }
}
