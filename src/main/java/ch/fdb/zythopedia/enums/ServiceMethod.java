package ch.fdb.zythopedia.enums;

import lombok.Getter;
import org.apache.logging.log4j.util.Strings;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum ServiceMethod {
    TAP("Pression"),
    BOTTLE("Bouteille");

    private final String label;

    ServiceMethod(String label) {
        this.label = label;
    }

    public static ServiceMethod getServiceMethod(String label) {
        if (Strings.isBlank(label)) {
            return null;
        }

        return findByEnumName(label)
                .orElseGet(() -> findByLabel(label)
                        .orElse(null));
    }

    private static Optional<ServiceMethod> findByEnumName(String label) {
        return Arrays.stream(ServiceMethod.values())
                .filter(method -> method.name().equalsIgnoreCase(label))
                .findFirst();
    }

    private static Optional<ServiceMethod> findByLabel(String label) {
        return Arrays.stream(ServiceMethod.values())
                .filter(method -> method.getLabel().equalsIgnoreCase(label))
                .findFirst();
    }

}
