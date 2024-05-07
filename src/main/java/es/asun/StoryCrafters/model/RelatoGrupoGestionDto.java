package es.asun.StoryCrafters.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelatoGrupoGestionDto {
    private int id;
    private double calificacion;
    private String feedback;
    private boolean aprobado;

    public void setAprobado(boolean aprobado) {
        this.aprobado = aprobado;
    }
}
