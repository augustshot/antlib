package ru.isu.antlib.model;

import lombok.Getter;

@Getter
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
