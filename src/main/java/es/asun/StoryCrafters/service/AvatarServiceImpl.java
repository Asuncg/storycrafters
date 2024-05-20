package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Avatar;
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
    public Optional<Avatar> findAvatarById(int avatarId) {
        return avatarRepository.findById(avatarId);
    }

    @Override
    public List<Avatar> findAllAvatars() {
        return avatarRepository.findAll();
    }
}
