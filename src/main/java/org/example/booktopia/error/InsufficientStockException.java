package org.example.booktopia.error;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super("Transaction declined insufficient stock");
    }
}
