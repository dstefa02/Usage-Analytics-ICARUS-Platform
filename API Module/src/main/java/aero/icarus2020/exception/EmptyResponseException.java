package aero.icarus2020.exception;

public class EmptyResponseException extends Exception {
    public EmptyResponseException() {
        System.out.println("The request has returned an empty body");
    }
}
