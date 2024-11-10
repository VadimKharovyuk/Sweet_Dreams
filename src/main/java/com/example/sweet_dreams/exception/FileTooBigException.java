package com.example.sweet_dreams.exception;

public class FileTooBigException extends RuntimeException {
    public FileTooBigException(String message) {
        super(message);
    }
}
