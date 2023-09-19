package com.linking.page_check.persistence;

import com.linking.page_check.domain.PageCheck;
import com.linking.page_check.dto.PageCheckRes;
import com.linking.page_check.dto.PageCheckUpdateRes;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PageCheckMapper {

    default PageCheckRes toDto(PageCheck source) {
        PageCheckRes.PageCheckResBuilder builder = PageCheckRes.builder();
        builder
                .pageCheckId(source.getId())
                .pageId(source.getId());
        if (source.getLastChecked() == null)
            builder
                    .isChecked(false)
                    .lastChecked("00-01-01 AM 00:00");
        else
            builder
                    .isChecked(true)
                    .lastChecked(source.getLastChecked());
        builder
                .userName(source.getParticipant().getUser().getFullName())
                .userId(source.getParticipant().getUser().getUserId());
        return builder.build();
    }

    default PageCheckUpdateRes toPageCheckUpdateDto(PageCheck source) {
        PageCheckUpdateRes.PageCheckUpdateResBuilder builder = PageCheckUpdateRes.builder();
        // pagecheck update이 되는 경우는 lastChecked가 null일 수 없지만 혹시 몰라 null 확인 처리.
        if (source.getLastChecked() == null)
            builder
                    .isChecked(false)
                    .lastChecked("00-01-01 AM 00:00");
        else
            builder
                    .isChecked(true)
                    .lastChecked(source.getLastChecked());
        builder
                .userId(source.getParticipant().getUser().getUserId());
        return builder.build();
    }


    default PageCheckRes toEmptyDto() {
        PageCheckRes builder = PageCheckRes.builder()
                .pageCheckId(-1L)
                .lastChecked("23-01-01 AM 01:01").build();
        return builder;
    }
//    default List<PageCheckRes> toDtoBulk(List<PageCheck> sources) {
//        if (sources == null) return null;
//
//        List<PageCheckRes> pageCheckResList = new ArrayList<>();
//
//        for (PageCheck source: sources) {
//            PageCheckRes.PageCheckResBuilder builder = PageCheckRes.builder();
//            builder
//                    .pageCheckId(source.getId())
//                    .pageId(source.getPage().getId())
//                    .lastChecked(source.getLastChecked().format(DateTimeFormatter.ofPattern("YY-MM-dd hh:mm a").withLocale(Locale.forLanguageTag("en"))))
//                    .userDetailedRes(null);
//
//            pageCheckResList.add(builder.build());
//        }
//        return pageCheckResList;
//    }
}
