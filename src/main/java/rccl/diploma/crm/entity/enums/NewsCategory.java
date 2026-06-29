package rccl.diploma.crm.entity.enums;

public enum NewsCategory {
    ANNOUNCEMENT("Объявление", "status-new"),
    EVENT("Событие", "status-active"),
    RENOVATION("Ремонт", "status-wait"),
    DANGER("Опасность", "status-danger"),
    CITY("Новости города", "status-review"),
    MONEY("Деньги", "status-done"),
    HEALTHCARE("Здоровье", "status-active"),
    WARNING("Предупреждение", "status-danger");
    private final String displayName;
    private final String badgeClass;

    NewsCategory(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() { return displayName; }
    public String getBadgeClass() { return badgeClass; }
}
