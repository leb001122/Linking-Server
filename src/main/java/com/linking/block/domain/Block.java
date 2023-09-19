package com.linking.block.domain;

import com.linking.annotation.domain.Annotation;
import com.linking.page.domain.Page;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "block")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Block {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "block_id")
    private Long id;
    
    // 필요여부에 따라 없앨 수 있음
    private int blockOrder;

    @Setter
    @NotNull
    @Column(length = 100)
    private String title;

    @Setter
    @NotNull
    @Column(columnDefinition = "TEXT")  // TEXT 타입은 65,535bytes
    private String content;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id")
    private Page page;

    @NotNull
    @OneToMany(mappedBy = "block", orphanRemoval = true)
    @OrderBy("createdDatetime asc")
    private List<Annotation> annotationList;



    public Block(String title, String content, Page page) {
        this.title = title;
        this.content = content;
        if (title == null) this.title = "untitled";
        if (content == null) this.content = "";
        setPage(page);
        this.blockOrder = order();
        this.annotationList = new ArrayList<>();
    }

    private void setPage(Page page) {
        if (this.page != null)
            this.page.getBlockList().remove(this);
        this.page = page;
        if (!page.getBlockList().contains(this)) {
            page.getBlockList().add(this);
        }
    }

    public void updateOrder(int order) {
        this.blockOrder = order;
    }

    private int order() {
        int order = 0;
        for (Block block : page.getBlockList()) {
            if (order <= block.getBlockOrder())
                order = block.getBlockOrder() + 1;
        }
        return order;
    }
}
