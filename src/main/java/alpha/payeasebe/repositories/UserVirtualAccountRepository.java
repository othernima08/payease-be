package alpha.payeasebe.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import alpha.payeasebe.models.User;
import alpha.payeasebe.models.UserVirtualAccount;
import alpha.payeasebe.payloads.res.ResponseShowVirtualAccount;
import alpha.payeasebe.payloads.res.ResponseShowVirtualAccountId;

public interface UserVirtualAccountRepository extends JpaRepository<UserVirtualAccount, String> {
    @Query(value = "SELECT uac.id, uac.number, p.name, p.profile_picture_url\r\n" + //
            "FROM user_virtual_accounts uac\r\n" + //
            "JOIN providers p ON uac.provider_id = p.id\r\n" + //
            "WHERE uac.user_id = ? AND uac.is_deleted = false", nativeQuery = true)
    List<ResponseShowVirtualAccount> getUserVirtualAccounts(String userId);

    @Query(value = "SELECT uv.id, uv.number, p.name as provider_name, p.profile_picture_url, p.is_deleted as provider_is_deleted "
            +
            "FROM user_virtual_accounts uv " +
            "INNER JOIN providers p ON uv.provider_id = p.id " +
            "WHERE uv.id = ? AND uv.is_deleted = false", nativeQuery = true)
    List<ResponseShowVirtualAccountId> getUserVirtualAccountById(String userVirtualAccountId);

    UserVirtualAccount findByNumber(String number);

    List<UserVirtualAccount> findByUser(User user);
}
