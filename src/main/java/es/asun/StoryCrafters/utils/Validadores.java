package es.asun.StoryCrafters.utils;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Usuario;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase de utilidad que proporciona métodos para validar datos.
 */
public class Validadores {

    /**
     * Valida si una cadena es un número entero.
     *
     * @param id La cadena a validar.
     * @return true si la cadena es un número entero, false de lo contrario.
     */
    public static Boolean validateId(final String id) {
        Boolean isNumber = Boolean.TRUE;
        try {
            Integer.parseInt(id);
        } catch (NumberFormatException nfe) {
            isNumber = Boolean.FALSE;
        }
        return isNumber;
    }

    /**
     * Verifica si un usuario es el gestor de un grupo.
     *
     * @param grupo   El grupo.
     * @param usuario El usuario.
     * @return true si el usuario es el gestor del grupo, false de lo contrario.
     */
    public static Boolean gestorValido(Grupo grupo, Usuario usuario) {
        return usuario.getId() == grupo.getUsuario().getId();
    }

    /**
     * Valida si una cadena es una dirección de correo electrónico válida.
     *
     * @param email La dirección de correo electrónico a validar.
     * @return true si la cadena es una dirección de correo electrónico válida, false de lo contrario.
     */
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
