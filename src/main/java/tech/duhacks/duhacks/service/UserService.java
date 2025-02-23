package tech.duhacks.duhacks.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tech.duhacks.duhacks.exception.AuthException;
import tech.duhacks.duhacks.model.User;
import tech.duhacks.duhacks.repository.UserRepo;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepo userRepo;

    private final UserMapper userMapper;

    public User signUp(User user) {

        userRepo.findOneByEmail(user.getEmail()).ifPresent(isUser -> {
            throw new AuthException("User with email %s already exists.".formatted(isUser.getEmail()));
        });

        return userRepo.save(user);
    }

    public User signIn(User user){
        var existingUser = userRepo.findOneByEmail(user.getEmail()).orElseThrow(() -> new EntityNotFoundException("User with email %s not found".formatted(user.getEmail())));

        if(!existingUser.getPassword().equals(user.getPassword()) ){
            throw  new AuthException("Password Mismatched");
        }

        return existingUser;
    }

    public void delete(Long id) {
        userRepo.deleteById(id);
    }

}
