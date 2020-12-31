package ch.fhnw.cpib.exceptions;

public class TypeError extends Exception {

    public TypeError(String location, String typeFound, String typeExpected) {
        super(String.format("'%s' found in '%s', expected '%s'", typeFound, location, typeExpected));
    }
}
