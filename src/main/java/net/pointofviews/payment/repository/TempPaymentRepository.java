package net.pointofviews.payment.repository;

import net.pointofviews.payment.domain.TempPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TempPaymentRepository extends JpaRepository<TempPayment, Long> {

    Optional<TempPayment> findByOrderId(String orderId);
}
