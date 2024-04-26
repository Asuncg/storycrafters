package es.asun.StoryCrafters.utilidades;

import es.asun.StoryCrafters.entity.Imagen;
import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.model.RelatoDto;

import java.util.Date;

public class Mappings {
    public static Relato mapToRelato(RelatoDto relatoDto, Usuario usuario, Imagen imagen) {
        // Mapear los datos del DTO al objeto Relato

        Relato relato = new Relato();
        relato.setId(relatoDto.getId());
        relato.setUsuario(usuario);
        relato.setTitulo(relatoDto.getTitulo());
        relato.setTexto(relatoDto.getTexto());
        relato.setFirmaAutor(relatoDto.getFirmaAutor());
        relato.setImagen(imagen);
        relato.setFechaCreacion(new Date());

        return relato;
    }
}
