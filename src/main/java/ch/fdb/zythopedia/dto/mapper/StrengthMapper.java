package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.enums.Strength;
import org.mapstruct.Mapper;

import java.util.Optional;

@Mapper
public interface StrengthMapper {
    default Integer toLong(Strength strength) {
        return Optional.ofNullable(strength).map(Strength::getRank).orElse(null);
    }
}
