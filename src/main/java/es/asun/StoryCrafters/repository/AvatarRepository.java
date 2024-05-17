package es.asun.StoryCrafters.repository;

import es.asun.StoryCrafters.entity.Avatar;
import es.asun.StoryCrafters.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  AvatarRepository extends JpaRepository<Avatar, Integer> {
}
