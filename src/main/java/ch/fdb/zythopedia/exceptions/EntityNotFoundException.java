package ch.fdb.zythopedia.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(long id, String entityClassName) {
        super(String.format("%s of id '%d' not found", entityClassName, id));
    }
}
