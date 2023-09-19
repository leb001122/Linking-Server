package com.linking.todo.domain;

import com.linking.assign.domain.Assign;
import com.linking.project.domain.Project;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@NamedEntityGraph(
        name = "Todo.fetchAssignAndParticipant",
        attributeNodes = {
                @NamedAttributeNode(value = "assignList", subgraph = "Assign.fetchParticipant")},
        subgraphs = {
                @NamedSubgraph(name = "Assign.fetchParticipant", attributeNodes = {@NamedAttributeNode(value = "participant")})})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "todo")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Long todoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "parent_todo_id")
    private Todo parentTodo;

    @Column(name = "is_parent", nullable = false)
    @ColumnDefault("true")
    private boolean isParent;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(nullable = false, length = 28)
    @ColumnDefault("")
    private String content;

    @OneToMany(mappedBy = "parentTodo", cascade = CascadeType.ALL)
    private List<Todo> childTodoList;

    @OneToMany(mappedBy = "todo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assign> assignList;

    public Todo(Long todoId){
        this.todoId = todoId;
    }

    public void setChildTodoList(List<Todo> childTodoList){
        this.childTodoList = childTodoList;
    }

    public void setAssignList(List<Assign> assignList) {
        this.assignList = assignList;
    }

}
