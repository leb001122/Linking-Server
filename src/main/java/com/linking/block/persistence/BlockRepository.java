package com.linking.block.persistence;

import com.linking.block.domain.Block;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {

    @Query("select b from Block b where b.page.id = :pageId order by b.blockOrder asc")
    List<Block> findAllByPageId(@Param("pageId") Long pageId);

    @EntityGraph(attributePaths = {"annotationList"}, type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT b FROM Block b WHERE b.page.id = :pageId order by b.blockOrder asc")
    List<Block> findAllByPageIdFetchAnnotations(@Param("pageId") Long pageId);

    @EntityGraph(attributePaths = {"annotationList"}, type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT b FROM Block b WHERE b.id = :blockId")
    Optional<Block> findByFetchAnnotations(@Param("blockId") Long blockId);

    @Query("select max(b.blockOrder) from Block b where b.page.id = :pageId")
    Integer findMaxBlockOrder(@Param("pageId") Long pageId);


}
