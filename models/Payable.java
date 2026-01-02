package models;
/**
 * Payable interface
 */
public interface Payable {
    void processPayment();

    default void applyDiscount() {
        System.out.println("Applying default discount.");
    }
    
}
