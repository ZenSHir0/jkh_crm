package rccl.diploma.crm.entity.enums;

@SuppressWarnings("LombokGetterMayBeUsed")
public enum Role {
    RESIDENT("Житель"),
    MASTER("Мастер"),
    ADMIN("Администратор");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
