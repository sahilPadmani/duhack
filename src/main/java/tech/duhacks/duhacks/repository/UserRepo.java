package tech.duhacks.duhacks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.duhacks.duhacks.model.User;

import java.util.Optional;


@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    public Optional<User> findOneByEmail(String email);
}
