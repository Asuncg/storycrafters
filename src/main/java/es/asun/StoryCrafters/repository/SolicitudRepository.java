package es.asun.StoryCrafters.repository;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Solicitud;
import es.asun.StoryCrafters.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {

    List<Solicitud> findByGrupo(Grupo grupo);


    Optional<Solicitud> findByGrupoAndUsuario(Grupo grupo, Usuario usuario);

    Solicitud findById(int id);

    boolean existsSolicitudByGrupo(Grupo grupo);
}
