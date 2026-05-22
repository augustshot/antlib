package ru.isu.antlib.service;

public class BookTimeoutException extends RuntimeException{
    public BookTimeoutException(String message) {
        super(message);
    }
}
