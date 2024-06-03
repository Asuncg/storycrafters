package es.asun.StoryCrafters.utils;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Usuario;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
