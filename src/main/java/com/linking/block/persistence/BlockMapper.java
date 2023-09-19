package com.linking.block.persistence;

import com.linking.annotation.dto.AnnotationRes;
import com.linking.block.dto.BlockDetailRes;
import com.linking.block.domain.Block;
import com.linking.block.dto.BlockRes;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface BlockMapper {


    default BlockRes toDto(Block source) {

        BlockRes.BlockResBuilder builder = BlockRes.builder();
        builder
                .pageId(source.getPage().getId())
                .blockId(source.getId())
                .title(source.getTitle())
                .content(source.getContent());

        return builder.build();
    }

    default BlockRes toBlockIdResDto(Long blockId) {
        return BlockRes.builder().blockId(blockId).build();
    }

    default BlockDetailRes toDto(Block source, List<AnnotationRes> annotationResList) {

        BlockDetailRes.BlockDetailResBuilder builder = BlockDetailRes.builder();
        builder
                .blockId(source.getId())
                .pageId(source.getPage().getId())
                .title(source.getTitle())
                .content(source.getContent())
                .annotationResList(annotationResList);

        return builder.build();
    }

    default List<BlockDetailRes> toDummyDto() {
        List<BlockDetailRes> blockResList = new ArrayList<>();
        List<AnnotationRes> annotationResList = new ArrayList<>();

        AnnotationRes annotationRes = AnnotationRes.builder()
                .annotationId(-1L)
                .blockId(-1L)
                .content("")
                .lastModified("00-01-01 AM 00:00")
                .userName("")
                .userId(-1L)
                .build();
        annotationResList.add(annotationRes);

        BlockDetailRes blockRes = BlockDetailRes.builder()
                .blockId(-1L)
                .pageId(-1L)
                .title("")
                .content("")
                .annotationResList(annotationResList)
                .build();
        blockResList.add(blockRes);

        return blockResList;
    }


}
