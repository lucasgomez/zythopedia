package ch.fdb.zythopedia.dto;

public interface EnumerableDto<D> {

    D setCurrentEditionAvailableCount(Integer count);
    D setCurrentEditionCount(Integer count);
}
