package com.linking.annotation.persistence;

import com.linking.annotation.domain.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, Long> {

    @Query("select a from Annotation a where a.block.id = :blockId")
    List<Annotation> findAllByBlockId(@Param("blockId") Long blockId);
}
