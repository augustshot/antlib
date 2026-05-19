package ru.isu.antlib.model;

import lombok.Getter;

@Getter
public enum Role {
    OWNER("Владелец"),
    MEMBER("Участник");

    private final String value;

    Role(final String value) {
        this.value = value;
    }
}
