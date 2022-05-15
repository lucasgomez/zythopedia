package ch.fdb.zythopedia.enums;

import lombok.Getter;

@Getter
public enum Availability {
    SOON("Prochainement"),
    AVAILABLE("Disponible"),
    OUT_OF_STOCK("Epuisé");

    private String description;

    Availability(String description) {
        this.description = description;
    }
}
