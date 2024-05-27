package es.asun.StoryCrafters.utils;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Usuario;

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

    public static Boolean gestorValido(Grupo grupo, Usuario usuario) {
        return usuario.getId() == grupo.getUsuario().getId();
    }
}
