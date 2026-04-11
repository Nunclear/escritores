package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.Volume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VolumeRepository extends JpaRepository<Volume, Integer> {
    Optional<Volume> findByIdAndStoryId(Integer id, Integer storyId);
}