package tech.duhacks.duhacks.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.duhacks.duhacks.model.HealthProduct;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HealthProductRepo extends JpaRepository<HealthProduct, Long> {

    List<HealthProduct> findAllByUserIdAndQuantityGreaterThanAndExpiryDateAfter(Long userId, Integer quantity, LocalDate currentDate);

    @Query("SELECT hp FROM HealthProduct hp WHERE hp.user.id = :userId " +
            "AND hp.quantity > 0 " +
            "AND hp.expiryDate > :expiryDate " +
            "AND hp.lowQuantity >= hp.quantity")
    List<HealthProduct> findAllByUserIdAndQuantityGreaterThanAndExpiryDateAfterAndLowQuantityGreaterThanEqual(
            @Param("userId") Long userId,
            @Param("expiryDate") LocalDate expiryDate);

    @Modifying
    @Transactional
    @Query("UPDATE HealthProduct h SET h.quantity = :quantity WHERE h.id = :id")
    void updateQuantityById(@Param("id") Long id, @Param("quantity") float quantity);

    List<HealthProduct> findAllByUserId(Long id);
}
