package com.linking.page.domain;

import com.linking.block.domain.Block;
import com.linking.group.domain.Group;
import com.linking.page_check.domain.PageCheck;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "page")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Page {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "page_id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    private int pageOrder;

    @NotNull
    private String title;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String content;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Template template;

    @NotNull
    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL)
    @OrderBy("blockOrder asc")
    private List<Block> blockList;

    @NotNull
    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL)
    private List<PageCheck> pageCheckList;

    @Builder
    public Page(String title, Group group, List<Block> blockList, List<PageCheck> pageCheckList, Template template) {
        setTitle(title);
        setGroup(group);
        setTemplate(template);
        this.blockList = blockList;
        this.pageCheckList = pageCheckList;
    }

    public void setGroup(Group group) {
        this.group = group;
        if (!group.getPageList().contains(this)) {
            group.getPageList().add(this);
        }
    }

    public void setTitle(String title) {
        this.title = title;
        if (title == null)
            this.title = "untitled";
    }

    private void setTemplate(Template template) {
        this.template = template;
        if (template == Template.BLANK)
            this.content = "";
    }

    @PrePersist
    public void prePersist(){
        this.blockList = this.blockList == null ? new ArrayList<>() : this.blockList;
        this.pageCheckList = this.pageCheckList == null ? new ArrayList<>() : this.pageCheckList;
    }

    public void updateOrder(int order) {
        this.pageOrder = order;
    }
}
