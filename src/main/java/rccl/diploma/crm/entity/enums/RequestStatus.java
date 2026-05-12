package rccl.diploma.crm.entity.enums;

public enum RequestStatus {
    NEW("Новая"),
    IN_PROGRESS("В работе"),
    DONE("Выполнена"),
    REJECTED("Отклонена"),
    CANCELLED("Отменена"),
    ON_HOLD("На паузе");  // если вдруг понадобится статус "ждём комплект документов" или что-то подобное

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
            case NEW -> "status-new";          // синий — новая
            case IN_PROGRESS -> "status-wait";  // жёлтый — в работе
            case DONE -> "status-active";         // зелёный — выполнена
            case REJECTED -> "status-danger";      // красный — отклонена
            case CANCELLED -> "status-done";  // серый — отменена
            case ON_HOLD -> "status-wait";         // голубой — на паузе
        };
    }
}
