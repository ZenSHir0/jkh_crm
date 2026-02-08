package rccl.diploma.crm.entity.enums;

public enum RequestType {
    HEATING_ISSUES("Отсутствие/некачественное отопление"),
    HOT_WATER_ISSUES("Проблемы с горячей водой"),
    COLD_WATER_ISSUES("Проблемы с холодной водой"),
    SEWERAGE_ISSUES("Засор/авария канализации"),
    ELEVATOR_ISSUES("Неисправность лифта"),
    LIGHTING_ISSUES("Неисправность освещения МОП"),
    ROOF_LEAKAGE("Протечка кровли / водостока"),
    VENTILATION_ISSUES("Проблемы с вентиляцией"),
    GAS_EQUIPMENT("Неисправность газового оборудования"),
    BUILDING_STRUCTURE("Повреждение конструкций дома"),
    ELECTRICITY_ISSUES("Проблемы с электроснабжением"),
    COMMON_AREA_MAINTENANCE("Ненадлежащее содержание МОП"),
    BILLING_ERRORS("Перерасчёт / ошибки в ЕПД"),
    MANAGEMENT_COMPLAINTS("Разъяснения и жалобы на УК/ТСЖ"),
    OTHER("Другое");

    private final String displayName;

    RequestType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}