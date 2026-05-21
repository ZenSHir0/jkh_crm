package rccl.diploma.crm.entity.enums;

public enum RequestStatus {
    NEW("Новая"),
    IN_PROGRESS("В работе"),
    PENDING_REVIEW("На проверке"),
    DONE("Выполнена"),
    REJECTED("Отклонена"),
    CANCELLED("Отменена"),
    ON_HOLD("На паузе");

    private final String displayName;

    RequestStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Опционально: метод для цвета в таблице (Bootstrap-классы)
    public String getBadgeClass() {
        return switch (this) {
            case NEW -> "status-new";
            case IN_PROGRESS -> "status-wait";
            case PENDING_REVIEW -> "status-review";
            case DONE -> "status-active";
            case REJECTED -> "status-danger";
            case CANCELLED -> "status-done";
            case ON_HOLD -> "status-wait";
        };
    }
}
