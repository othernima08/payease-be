package alpha.payeasebe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import alpha.payeasebe.models.UserVirtualAccount;

public interface UserVirtualAccountRepository extends JpaRepository<UserVirtualAccount,String> {
    UserVirtualAccount findByNumber(String number);
}
