package ru.isu.antlib.model;

public enum Status {
    READING("Читаю"),
    PLANNED("Буду читать"),
    POSTPONED("Отложено"),
    FINISHED("Прочитано");

    private final String value;

    Status(final String value) {
        this.value = value;
    }
}
