package com.github.kerner1000.etoro.stats.thymeleaf;

public class StorageFileNotFoundException extends Exception {

    public StorageFileNotFoundException() {
    }

    public StorageFileNotFoundException(String message) {
        super(message);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageFileNotFoundException(Throwable cause) {
        super(cause);
    }

    public StorageFileNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
