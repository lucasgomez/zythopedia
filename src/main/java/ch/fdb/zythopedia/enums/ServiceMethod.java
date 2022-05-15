package ch.fdb.zythopedia.enums;

import lombok.Getter;

@Getter
public enum ServiceMethod {
    TAP("Pression"),
    BOTTLE("Bouteille");

    private String label;

    ServiceMethod(String label) {
        this.label = label;
    }
}
