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

}
