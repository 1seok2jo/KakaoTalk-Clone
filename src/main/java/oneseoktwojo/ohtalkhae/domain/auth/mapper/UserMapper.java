package oneseoktwojo.ohtalkhae.domain.auth.mapper;

import oneseoktwojo.ohtalkhae.domain.auth.User;
import oneseoktwojo.ohtalkhae.domain.auth.dto.UserRegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    User toUser(UserRegisterRequest request);
}
