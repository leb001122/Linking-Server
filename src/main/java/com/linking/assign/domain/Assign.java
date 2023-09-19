package com.linking.assign.domain;

import com.linking.participant.domain.Participant;
import com.linking.todo.domain.Todo;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@NamedEntityGraph(
        name = "Assign.fetchTodoAndParticipant",
        attributeNodes = {
                @NamedAttributeNode(value = "todo", subgraph = "Todo.fetchProject"),
                @NamedAttributeNode(value = "participant", subgraph = "Participant.fetchUser")},
        subgraphs = {
                @NamedSubgraph(name = "Participant.fetchUser", attributeNodes = {@NamedAttributeNode(value = "user")}),
                @NamedSubgraph(name = "Todo.fetchProject", attributeNodes = {@NamedAttributeNode(value = "project")})})
@NamedEntityGraph(
        name = "Assign.fetchTodoAndChildTodo",
        attributeNodes = {
                @NamedAttributeNode(value = "todo", subgraph = "Todo.fetchProjectAndChild"),
                @NamedAttributeNode(value = "participant", subgraph = "Participant.fetchUser")},
        subgraphs = {
                @NamedSubgraph(name = "Participant.fetchUser", attributeNodes = {@NamedAttributeNode(value = "user")}),
                @NamedSubgraph(name = "Todo.fetchProjectAndChild", attributeNodes = {@NamedAttributeNode(value = "childTodoList")})})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "assign")
public class Assign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assign_id")
    private Long assignId;

    @ManyToOne
    @JoinColumn(name = "todo_id", nullable = false)
    private Todo todo;

    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 20)
    @ColumnDefault("BEFORE_START")
    private Status status;

}
