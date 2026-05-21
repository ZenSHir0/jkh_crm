package rccl.diploma.crm.entity.enums;

public enum TransactionType {
    CREDIT("Начисление за заявку"),
    BONUS("Бонус"),
    FINE("Штраф"),
    PAYOUT("Выплата");

    private final String displayName;

    TransactionType(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
}
