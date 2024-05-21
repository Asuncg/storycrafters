package es.asun.StoryCrafters.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

import static es.asun.StoryCrafters.utilidades.Constantes.ESTADO_APROBADO;

@Getter
@Setter
@Entity
public class RelatoGrupo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_relato")
    private Relato relato;

    @ManyToOne
    @JoinColumn(name = "id_grupo")
    private Grupo grupo;

    @Column(nullable = false, name = "titulo", length = 150)
    private String titulo;

    @Column(nullable = false, name = "texto", length = 10000)
    private String texto;

    @ManyToOne
    @JoinColumn(name = "id_imagen")
    private Imagen imagen;

    @ManyToMany
    @JoinTable(
            name = "relatogrupo_categoria",
            joinColumns = @JoinColumn(name = "relato_grupo_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private List<Categoria> categorias;

    @Column(nullable = false, name = "firmaAutor", length = 60)
    private String firmaAutor;

    @Column(name = "estado")
    private int estado = 1;

    @Column(name = "feedback", length = 500)
    private String feedback;

    // Nombre de la columna de calificación
    @Column(name = "calificacion")
    private double calificacion;

    // Formato de fecha y hora
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaPublicacion;

    // Formato de fecha y hora
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEnvio;

    // Método para generar la fecha de publicación automáticamente
    @PreUpdate
    private void preUpdate() {
        if (this.estado == ESTADO_APROBADO && this.fechaPublicacion == null) {
            this.fechaPublicacion = new Date();
        }
    }
}

