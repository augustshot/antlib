package ru.isu.antlib.exception;

public class BookTimeoutException extends RuntimeException{
    public BookTimeoutException(String message) {
        super(message);
    }
}
