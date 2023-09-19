package com.linking.page.persistence;

import com.linking.block.dto.BlockDetailRes;
import com.linking.group.domain.Group;
import com.linking.page.dto.BlankPageDetailRes;
import com.linking.page.dto.BlockPageDetailRes;
import com.linking.page.dto.PageCreateReq;
import com.linking.page.dto.PageRes;
import com.linking.page_check.dto.PageCheckRes;
import com.linking.page.domain.Page;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PageMapper {

    default PageRes toDto(Page source, int annoNotCnt) {

        PageRes.PageResBuilder builder = PageRes.builder();
        builder
                .pageId(source.getId())
                .title(source.getTitle())
                .groupId(source.getGroup().getId())
                .template(source.getTemplate())
                .annoNotCnt(annoNotCnt);

        return builder.build();
    }

    default BlockPageDetailRes toDto(
            Page source, List<BlockDetailRes> blockResList, List<PageCheckRes> pageCheckResList)
    {
        BlockPageDetailRes builder = BlockPageDetailRes.builder()
                .pageId(source.getId())
                .title(source.getTitle())
                .groupId(source.getGroup().getId())
                .pageCheckResList(pageCheckResList)
                .blockResList(blockResList)
                .build();

        return builder;
    }

    default BlankPageDetailRes toDto(
            Page source, List<PageCheckRes> pageCheckResList)
    {
        BlankPageDetailRes builder = BlankPageDetailRes.builder()
                .pageId(source.getId())
                .title(source.getTitle())
                .content(source.getContent())
                .groupId(source.getGroup().getId())
                .pageCheckResList(pageCheckResList)
                .build();

        return builder;

    }

    default Page toEntity(PageCreateReq source, Group group) {

        Page.PageBuilder builder = Page.builder();
        builder
                .title(source.getTitle())
                .template(source.getTemplate())
                .group(group);

        return builder.build();
    }
}
