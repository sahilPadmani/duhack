package tech.duhacks.duhacks.service;

import org.springframework.stereotype.Service;
import tech.duhacks.duhacks.dto.UserDto;
import tech.duhacks.duhacks.model.User;

@Service
public class UserMapper {

    public UserDto getUserRes (User user){
        return new UserDto(user.getId());
    }
}
