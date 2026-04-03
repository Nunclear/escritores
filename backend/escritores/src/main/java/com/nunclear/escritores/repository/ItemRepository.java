package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByStoryId(Integer storyId);
}
