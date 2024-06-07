package es.asun.StoryCrafters.repository;

import es.asun.StoryCrafters.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface  AvatarRepository extends JpaRepository<Avatar, Integer> {
    @Override
    Optional<Avatar> findById(Integer integer);
}
