package alpha.payeasebe.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import alpha.payeasebe.models.User;
import alpha.payeasebe.payloads.res.ResponseShowUsersNotNullAndNotUser;

public interface UserRepository extends JpaRepository<User,String> {
    Boolean existsByEmail(String email);
    Boolean existsByPhoneNumber(String phoneNumber);
    User findByEmail(String email);
    User findByPhoneNumber(String phoneNumber);

@Query(value = "SELECT * FROM users WHERE id <> ? AND phone_number IS NOT NULL;", nativeQuery = true)
List<ResponseShowUsersNotNullAndNotUser> getUserNotNull(String id);

}
