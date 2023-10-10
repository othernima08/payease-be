package alpha.payeasebe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import alpha.payeasebe.models.User;

public interface UserRepository extends JpaRepository<User,String> {
    Boolean existsByEmail(String email);
    Boolean existsByPhoneNumber(String phoneNumber);
    User findByEmail(String email);
    User findByPhoneNumber(String phoneNumber);

    
}
