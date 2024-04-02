package es.asun.StoryCrafters.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Relato")
public class Relato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "titulo", length = 100)
    private String titulo;

    @Column(name = "texto", length = 4000)
    private String texto;

    @ManyToMany
    private List<Categoria> categorias;

    @Column(name = "firmaAutor", length = 60)
    private String firmaAutor;

}

