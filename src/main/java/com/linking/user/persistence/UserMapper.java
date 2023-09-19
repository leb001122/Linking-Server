package com.linking.user.persistence;

import com.linking.user.domain.User;
import com.linking.user.dto.UserDetailedRes;
import com.linking.user.dto.UserSignUpReq;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    UserDetailedRes toDto(User user);
    List<UserDetailedRes> toDto(List<User> list);
    User toEntity(UserSignUpReq userSignUpReq);

}
