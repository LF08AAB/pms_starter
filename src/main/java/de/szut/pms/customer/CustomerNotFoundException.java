package de.szut.pms.customer;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long id) {
        super("Es gibt keinen Kunden mit der Kennung " + id + ".");
    }
}
