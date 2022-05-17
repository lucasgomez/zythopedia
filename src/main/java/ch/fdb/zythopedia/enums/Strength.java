package ch.fdb.zythopedia.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

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
            return getStrengthByRank(Integer.parseInt(rank));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    public static Strength getStrengthByRank(int rank) {
        return Arrays.stream(Strength.values())
                .filter(strength -> strength.getRank() == rank)
                .findFirst()
                .orElse(null);
    }
}
