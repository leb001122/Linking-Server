package com.linking.page.persistence;

import com.linking.page.domain.Page;
import com.linking.page.domain.Template;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {

    @Query(value = "select p from Page p where p.group.id = :groupId order by p.pageOrder asc")
    List<Page> findAllByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT p FROM Page p JOIN FETCH p.pageCheckList WHERE p.id = :pageId")
    Optional<Page> findByIdFetchPageChecks(@Param("pageId") Long pageId);

    @EntityGraph(attributePaths = {"blockList"}, type = EntityGraph.EntityGraphType.FETCH)
    @Query(value = "SELECT p FROM Page p WHERE p.id = :pageId")
    Optional<Page> findByIdFetchBlocks(@Param("pageId") Long pageId);

    @Query("SELECT p.group.id FROM Page p WHERE p.id = :pageId")
    Long getGroupIdByPageId(@Param("pageId") Long pageId);

    @Query(value = "SELECT p FROM Page p where p.template = :template")
    List<Page> findByTemplate(@Param("template") Template template);

    @Query("select max(p.pageOrder) from Page p where p.group.id = :groupId")
    Integer findMaxPageOrder(@Param("groupId") Long groupId);
}
