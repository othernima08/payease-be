package alpha.payeasebe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import alpha.payeasebe.models.TopUp;

public interface  TopUpRepository extends JpaRepository<TopUp, String> {
    TopUp findByPaymentCode(String paymentCode);
}
