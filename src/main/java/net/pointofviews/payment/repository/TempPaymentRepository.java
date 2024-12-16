package net.pointofviews.payment.repository;

import net.pointofviews.payment.domain.TempPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TempPaymentRepository extends JpaRepository<TempPayment, Long> {

    Optional<TempPayment> findByOrderId(String orderId);

    boolean existsByMemberIdAndOrderId(UUID memberId, String orderId);

    void deleteByOrderId(String orderId);
}
