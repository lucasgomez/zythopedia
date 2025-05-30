package ch.fdb.zythopedia.entity;

public interface NamedEntity {
    Long getId();

    String getName();

    default String getDescription() {
        return null;
    }
}
