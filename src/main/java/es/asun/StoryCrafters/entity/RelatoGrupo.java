package es.asun.StoryCrafters.entity;


import jakarta.persistence.*;
import java.util.Date;

@Entity
public class RelatoGrupo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_relato")
    private Relato relato;

    @ManyToOne
    @JoinColumn(name = "id_grupo")
    private Grupo grupo;

    @Column(nullable = false, name = "titulo", length = 50)
    private String titulo;

    @Column(nullable = false, name = "texto", length = 50)
    private String texto;

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

    // Método para generar la fecha de publicación automáticamente
    @PreUpdate
    private void preUpdate() {
        if (this.estado == 2 && this.fechaPublicacion == null) {
            this.fechaPublicacion = new Date();
        }
    }
}

