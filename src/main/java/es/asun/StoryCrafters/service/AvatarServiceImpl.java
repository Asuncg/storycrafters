package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Avatar;
import es.asun.StoryCrafters.exceptions.AvatarNotFoundException;
import es.asun.StoryCrafters.repository.AvatarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AvatarServiceImpl implements AvatarService{

    @Autowired
    private AvatarRepository avatarRepository;

    @Override
    public Avatar findAvatarById(int avatarId) throws AvatarNotFoundException {
        Optional<Avatar> avatarOptional = avatarRepository.findById(avatarId);

        if (avatarOptional.isEmpty()) {
            throw new AvatarNotFoundException("Avatar no encontrado");
        }

        return avatarOptional.get();
    }

    @Override
    public List<Avatar> findAllAvatars() {
        return avatarRepository.findAll();
    }
}
