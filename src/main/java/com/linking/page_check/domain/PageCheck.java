package com.linking.page_check.domain;

import com.linking.participant.domain.Participant;
import com.linking.page.domain.Page;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Entity
@Table(name = "pagecheck")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PageCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pagecheck_id")
    private Long id;

    // nullable
    private LocalDateTime lastChecked;

    private int annoNotCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id")
    private Participant participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id")
    private Page page;

    public PageCheck(Participant participant, Page page) {
        setParticipant(participant);
        setPage(page);
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public void setPage(Page page) {
        this.page = page;
        if (!page.getPageCheckList().contains(this)) {
            page.getPageCheckList().add(this);
        }
    }

    public void updateLastChecked() {
        this.lastChecked = LocalDateTime.now();
    }

    public void resetAnnoNotCount() {
        this.annoNotCount = 0;
    }

    public void increaseAnnotNotCount() {
        this.annoNotCount++;
    }

    public void reduceAnnoNotCount() {
        this.annoNotCount--;
    }

    public String getLastChecked() {
        if (lastChecked == null) return null;
        return lastChecked.format(DateTimeFormatter.ofPattern("YY-MM-dd a HH:mm").withLocale(Locale.forLanguageTag("en")));
    }
}
