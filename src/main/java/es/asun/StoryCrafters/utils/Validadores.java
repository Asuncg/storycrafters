package es.asun.StoryCrafters.utils;

public class Validadores {
    public static Boolean validateId(final String id) {
        Boolean isNumber = Boolean.TRUE;
        try {
            Integer.parseInt(id);
        } catch (NumberFormatException nfe) {
            isNumber = Boolean.FALSE;
        }
        return isNumber;
    }
}
