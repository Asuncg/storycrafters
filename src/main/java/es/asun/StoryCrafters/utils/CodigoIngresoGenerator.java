package es.asun.StoryCrafters.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class CodigoIngresoGenerator {
    private static final int LONGITUD_CODIGO = 8;

    public static String generarCodigoIngreso() {
        SecureRandom random = new SecureRandom();
        byte[] codigoBytes = new byte[LONGITUD_CODIGO];
        random.nextBytes(codigoBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codigoBytes);
    }

}
