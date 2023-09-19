package com.linking.group.domain;

import com.linking.project.domain.Project;
import com.linking.page.domain.Page;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "\"group\"")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group  {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private int groupOrder;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    @OrderBy("pageOrder asc")
    private List<Page> pageList;


    @Builder
    public Group(String name, Project project, List<Page> pageList) {
        this.name = name;
        setProject(project);
        this.pageList = pageList;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @PrePersist
    public void prePersist() {
        this.name = this.name == null ? "New Group" : this.name;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateOrder(int groupOrder) {
        this.groupOrder = groupOrder;
    }
}

