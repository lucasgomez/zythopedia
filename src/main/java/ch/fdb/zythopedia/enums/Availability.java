package ch.fdb.zythopedia.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Availability {
    SOON("Prochainement"),
    AVAILABLE("Disponible"),
    OUT_OF_STOCK("Epuisé");

    private String description;

    Availability(String description) {
        this.description = description;
    }

    public static Availability from(String availabilityName) {
        return Arrays.stream(Availability.values())
                .filter(val -> val.name().equalsIgnoreCase(availabilityName))
                .findFirst()
                .orElseThrow();
    }
}
