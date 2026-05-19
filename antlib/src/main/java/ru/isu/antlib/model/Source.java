package ru.isu.antlib.model;

public enum Source {
    EBOOK("Электронная книга"),
    SHARED("Из общей библиотеки"),
    OWNED("Моя книга"),
    BORROWED("Чужая книга");

    private final String value;

    Source(final String value) {
        this.value = value;
    }
}
