package es.asun.StoryCrafters.repository;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GrupoRepository extends JpaRepository<Grupo, Integer> {
    @Query("SELECT g FROM Grupo g JOIN g.usuarios u WHERE u = :usuario")
    List<Grupo> findAllByUsuario(Usuario usuario);

    Optional<Grupo> findByCodigoAcceso(String codigoAcceso);

    //List<Grupo> findRelatoGrupoByGrupo(Grupo grupo);


}

