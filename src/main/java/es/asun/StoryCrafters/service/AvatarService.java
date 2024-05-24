package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Avatar;
import es.asun.StoryCrafters.exceptions.AvatarNotFoundException;

import java.util.List;
import java.util.Optional;

public interface AvatarService {
    Avatar findAvatarById(int avatarId) throws AvatarNotFoundException;

    List<Avatar> findAllAvatars();
}
