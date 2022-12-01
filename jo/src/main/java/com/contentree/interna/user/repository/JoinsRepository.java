package com.contentree.interna.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.contentree.interna.user.entity.Joins;

@Repository
public interface JoinsRepository extends JpaRepository<Joins, Long> {
	Optional<Joins> findByJoinsId(String joinsId);
}
