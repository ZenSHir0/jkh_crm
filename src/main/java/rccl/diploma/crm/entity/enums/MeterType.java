package rccl.diploma.crm.entity.enums;

public enum MeterType {

    COLD_WATER("Холодная вода", "м³", "bi-droplet-fill", "#4DA8DA"),
    HOT_WATER("Горячая вода", "м³", "bi-thermometer-half", "#E8774A"),
    GAS("Газ", "м³", "bi-fire", "#E8A520"),
    ELECTRICITY("Электроэнергия", "кВт·ч", "bi-lightning-charge-fill", "#8B7FF5");

    private final String displayName;
    private final String unit;
    private final String icon;
    private final String color;

    MeterType(String displayName, String unit, String icon, String color) {
        this.displayName = displayName;
        this.unit = unit;
        this.icon = icon;
        this.color = color;
    }

    public String getDisplayName() { return displayName; }
    public String getUnit()        { return unit; }
    public String getIcon()        { return icon; }
    public String getColor()       { return color; }
}
