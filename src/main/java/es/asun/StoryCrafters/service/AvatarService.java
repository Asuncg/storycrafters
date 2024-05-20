package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Avatar;

import java.util.List;
import java.util.Optional;

public interface AvatarService {
    Optional<Avatar> findAvatarById(int avatarId);

    List<Avatar> findAllAvatars();
}
