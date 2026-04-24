package com.spring.jwt.subscription.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility service for generating unique transaction IDs and receipt numbers.
 * Thread-safe implementation with multiple formats.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Component
@Slf4j
public class TransactionIdGenerator {

    private static final String TRANSACTION_PREFIX = "TXN";
    private static final String RECEIPT_PREFIX = "RCP";
    private static final String OFFLINE_PREFIX = "OFF";
    
    private static final AtomicLong transactionCounter = new AtomicLong(0);
    private static final AtomicLong receiptCounter = new AtomicLong(0);
    
    private static final SecureRandom random = new SecureRandom();
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * Generate transaction ID for offline payment
     * Format: OFF-YYYYMMDD-HHMMSS-XXXXX
     * Example: OFF-20260122-143025-A7B9C
     */
    public String generateOfflineTransactionId() {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timePart = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        String randomPart = generateRandomAlphanumeric(5);
        
        String transactionId = String.format("%s-%s-%s-%s", 
                OFFLINE_PREFIX, datePart, timePart, randomPart);
        
        log.debug("Generated offline transaction ID: {}", transactionId);
        return transactionId;
    }

    /**
     * Generate transaction ID for online payment (future use)
     * Format: TXN-YYYYMMDD-XXXXX-NNNNN
     * Example: TXN-20260122-A7B9C-00123
     */
    public String generateOnlineTransactionId() {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = generateRandomAlphanumeric(5);
        long counter = transactionCounter.incrementAndGet() % 100000;
        String counterPart = String.format("%05d", counter);
        
        String transactionId = String.format("%s-%s-%s-%s", 
                TRANSACTION_PREFIX, datePart, randomPart, counterPart);
        
        log.debug("Generated online transaction ID: {}", transactionId);
        return transactionId;
    }

    /**
     * Generate receipt number
     * Format: RCP-YYYY-NNNNNNN
     * Example: RCP-2026-0001234
     */
    public String generateReceiptNumber() {
        LocalDateTime now = LocalDateTime.now();
        String yearPart = now.format(DateTimeFormatter.ofPattern("yyyy"));
        long counter = receiptCounter.incrementAndGet() % 10000000;
        String counterPart = String.format("%07d", counter);
        
        String receiptNumber = String.format("%s-%s-%s", 
                RECEIPT_PREFIX, yearPart, counterPart);
        
        log.debug("Generated receipt number: {}", receiptNumber);
        return receiptNumber;
    }

    /**
     * Generate transaction ID based on payment mode
     */
    public String generateTransactionId(String paymentMode) {
        if (isOfflinePayment(paymentMode)) {
            return generateOfflineTransactionId();
        } else {
            return generateOnlineTransactionId();
        }
    }

    /**
     * Check if payment mode is offline
     */
    private boolean isOfflinePayment(String paymentMode) {
        return paymentMode != null && 
               (paymentMode.equalsIgnoreCase("OFFLINE") ||
                paymentMode.equalsIgnoreCase("CASH") ||
                paymentMode.equalsIgnoreCase("CHEQUE") ||
                paymentMode.equalsIgnoreCase("BANK_TRANSFER"));
    }

    /**
     * Generate random alphanumeric string
     */
    private String generateRandomAlphanumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALPHANUMERIC.length());
            sb.append(ALPHANUMERIC.charAt(index));
        }
        return sb.toString();
    }

    /**
     * Validate transaction ID format
     */
    public boolean isValidTransactionId(String transactionId) {
        if (transactionId == null || transactionId.isEmpty()) {
            return false;
        }
        
        // Check if it matches any of our formats
        return transactionId.matches("^(TXN|OFF|RCP)-\\d{8}-[A-Z0-9]{5,6}(-[A-Z0-9]{5})?$") ||
               transactionId.matches("^RCP-\\d{4}-\\d{7}$");
    }

    /**
     * Extract date from transaction ID
     */
    public LocalDateTime extractDateFromTransactionId(String transactionId) {
        try {
            String[] parts = transactionId.split("-");
            if (parts.length >= 2) {
                String datePart = parts[1];
                if (datePart.length() == 8) {
                    return LocalDateTime.parse(datePart + "000000", 
                            DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                }
            }
        } catch (Exception e) {
            log.warn("Failed to extract date from transaction ID: {}", transactionId);
        }
        return null;
    }
}
