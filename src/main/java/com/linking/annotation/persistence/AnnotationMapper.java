package com.linking.annotation.persistence;

import com.linking.annotation.domain.Annotation;
import com.linking.annotation.dto.AnnotationRes;
import com.linking.annotation.dto.AnnotationCreateReq;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AnnotationMapper {

    default AnnotationRes toDto(Annotation source) {
        AnnotationRes.AnnotationResBuilder builder = AnnotationRes.builder();
        builder
                .annotationId(source.getId())
                .blockId(source.getBlock().getId())
                .content(source.getContent())
                .lastModified(source.getLastModified())
                .userId(source.getParticipant().getUser().getUserId())
                .userName(source.getWriter());

        return builder.build();
    }

    default AnnotationRes toDummyDto() {
        AnnotationRes annotationRes = AnnotationRes.builder()
                .annotationId(-1L)
                .blockId(-1L)
                .content("")
                .lastModified("00-01-01 AM 00:00")
                .userId(-1L)
                .userName("")
                .build();

        return annotationRes;
    }

    default Annotation toEntity(AnnotationCreateReq source) {
        if (source == null) {
            return null;
        }
        Annotation.AnnotationBuilder builder = Annotation.builder();
        builder
                .content(source.getContent())
                .lastModified(LocalDateTime.now());

        return builder.build();
    }
}
