package es.asun.StoryCrafters.repository;

import es.asun.StoryCrafters.entity.Avatar;
import es.asun.StoryCrafters.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface  AvatarRepository extends JpaRepository<Avatar, Integer> {
    @Override
    Optional<Avatar> findById(Integer integer);
}
