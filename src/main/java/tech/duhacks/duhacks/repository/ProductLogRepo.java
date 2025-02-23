package tech.duhacks.duhacks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.duhacks.duhacks.model.ProductLog;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductLogRepo extends JpaRepository<ProductLog,Long> {

    @Query("SELECT p FROM ProductLog p WHERE p.user.id = :userId AND p.createdAt > :date")
    List<ProductLog> findAllByUserIdAndCreatedAtIsAfter(@Param("userId") Long userId, @Param("date") LocalDateTime date);

    List<ProductLog> findAllByUserIdAndCreatedAtBetween(Long id, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
