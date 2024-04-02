package es.asun.StoryCrafters.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "Usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(nullable = false, name = "nombre", length = 60)
    private String firstName;

    @Column(name = "apellidos", length = 60)
    private String lastName;

    @Column(name = "firma", length = 60)
    private String firmaAutor;

    @Column(nullable = false, name = "email", unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(name = "activo", length = 1, columnDefinition = "int default 1")
    private boolean activo = true;

    @ManyToMany
    private List<Grupo> grupos;

    public Usuario(String nombre, String password) {
    }
}
