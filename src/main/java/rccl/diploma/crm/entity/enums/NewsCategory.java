package rccl.diploma.crm.entity.enums;

public enum NewsCategory {
    ANNOUNCEMENT("Объявление"),
    EVENT("Событие"),
    RENOVATION("Ремонтные работы"),
    DANGER("Опасность"),
    CITY("Новости города"),
    MONEY("Деньги"),
    HEALTHCARE("Здоровье"),
    WARNING("Предупреждение");

    private final String displayName;

    NewsCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }
}
