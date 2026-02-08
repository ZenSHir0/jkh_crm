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
            case NEW -> "bg-primary";          // синий — новая
            case IN_PROGRESS -> "bg-warning";  // жёлтый — в работе
            case DONE -> "bg-success";         // зелёный — выполнена
            case REJECTED -> "bg-danger";      // красный — отклонена
            case CANCELLED -> "bg-secondary";  // серый — отменена
            case ON_HOLD -> "bg-info";         // голубой — на паузе
        };
    }
}
