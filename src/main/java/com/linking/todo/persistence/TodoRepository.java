package com.linking.todo.persistence;

import com.linking.project.domain.Project;
import com.linking.todo.domain.Todo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    @EntityGraph(value = "Todo.fetchAssignAndParticipant", type = EntityGraph.EntityGraphType.FETCH)
    Optional<Todo> findByTodoId(@Param("id") Long id);

    @EntityGraph(value = "Todo.fetchAssignAndParticipant", type = EntityGraph.EntityGraphType.FETCH)
    @Query(value = "SELECT t FROM Todo t" +
            " WHERE :projectId = t.project.projectId" +
            " AND function('date_format', :today, '%Y%m%d') = function('date_format', t.dueDate, '%Y%m%d')")
    List<Todo> findByProjectAndMonth(@Param("projectId") Long projectId, @Param("today") LocalDate today);

    @EntityGraph(value = "Todo.fetchAssignAndParticipant", type = EntityGraph.EntityGraphType.FETCH)
    @Query(value = "SELECT t FROM Todo t" +
            " WHERE :projectId = t.project.projectId" +
            " AND function('date_format', :date, '%Y%m%d')" +
            " BETWEEN function('date_format', t.startDate, '%Y%m%d') AND function('date_format', t.dueDate, '%Y%m%d')")
    List<Todo> findByProjectAndDateContains(@Param("projectId") Long projectId, @Param("date") LocalDate date);

    @EntityGraph(value = "Todo.fetchAssignAndParticipant", type = EntityGraph.EntityGraphType.FETCH)
    @Query(value = "SELECT t FROM Todo t" +
            " WHERE :project = t.project" +
            " AND function('date_format', :date, '%Y%m')" +
            " BETWEEN function('date_format', t.startDate, '%Y%m') AND function('date_format', t.dueDate, '%Y%m')")
    List<Todo> findByProjectAndMonthContains(@Param("project") Project project, @Param("date") LocalDate date);

}
