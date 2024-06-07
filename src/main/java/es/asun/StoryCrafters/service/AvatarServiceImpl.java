package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Avatar;
import es.asun.StoryCrafters.exceptions.AvatarNotFoundException;
import es.asun.StoryCrafters.repository.AvatarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de gestión de avatares.
 */
@Service
public class AvatarServiceImpl implements AvatarService {

    /**
     * Implementación del servicio de gestión de avatares.
     */
    @Autowired
    private AvatarRepository avatarRepository;

    /**
     * Busca un avatar por su identificador único.
     * @param avatarId Identificador único del avatar.
     * @return El avatar encontrado.
     * @throws AvatarNotFoundException Si no se encuentra el avatar con el identificador dado.
     */
    @Override
    public Avatar findAvatarById(int avatarId) throws AvatarNotFoundException {
        Optional<Avatar> avatarOptional = avatarRepository.findById(avatarId);

        if (avatarOptional.isEmpty()) {
            throw new AvatarNotFoundException("Avatar no encontrado");
        }

        return avatarOptional.get();
    }

    /**
     * Obtiene todos los avatares disponibles.
     * @return Lista de avatares.
     */
    @Override
    public List<Avatar> findAllAvatars() {
        return avatarRepository.findAll();
    }
}
