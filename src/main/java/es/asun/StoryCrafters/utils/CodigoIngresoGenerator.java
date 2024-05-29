package es.asun.StoryCrafters.utils;

import es.asun.StoryCrafters.service.GrupoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CodigoIngresoGenerator {

    private static final int LONGITUD_CODIGO = 8;
    private static final String CARACTERES = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generarCodigoIngreso(GrupoService grupoService) {
        String codigo;
        do {
            StringBuilder codigoBuilder = new StringBuilder(LONGITUD_CODIGO);
            for (int i = 0; i < LONGITUD_CODIGO; i++) {
                int index = random.nextInt(CARACTERES.length());
                codigoBuilder.append(CARACTERES.charAt(index));
            }
            codigo = codigoBuilder.toString();
        } while (grupoService.existeCodigoAcceso(codigo));

        return codigo;
    }
}
