package org.example.booktopia.repository;

import jakarta.validation.constraints.NotNull;
import org.example.booktopia.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("select c.id from Category c where c.id in :categoryIds and c.isDeleted = false")
    List<Long> findAllIdByAndIsDeletedIsFalse(@Param("categoryIds") List<Long> categoryIds);

    @Override
    @Query("select c from Category c where c.isDeleted = false and c.id = :id")
    Optional<Category> findById(@Param("id") Long id);

    @Query("select count(c) > 0 from Category c where c.name = :name")
    Boolean existsByName(String name);

    @Query("select c from Category c where c.name = :name")
    Category findByName(String name);

    @Query("select c from Category c where c.isDeleted = false")
    List<Category> findAllAvailableCategories();
}
