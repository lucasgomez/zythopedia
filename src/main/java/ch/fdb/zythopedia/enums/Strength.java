package ch.fdb.zythopedia.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
@Getter
public enum Strength {
    N_A(0),
    LOW(1),
    MILD(2),
    STRONG(3),
    VERY_STRONG(4),
    UBER(5);

    private int rank;

    public static Strength getStrengthByRank(String rank) {
        try {
            return getStrengthByRank(Long.parseLong(rank));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    public static Strength getStrengthByRank(Long rank) {
        return Optional.ofNullable(rank)
                .flatMap(value -> Arrays.stream(Strength.values()).
                        filter(strength -> strength.getRank() == value)
                        .findFirst())
                .orElse(null);
    }
}
