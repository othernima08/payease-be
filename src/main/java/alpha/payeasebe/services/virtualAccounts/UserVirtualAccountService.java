package alpha.payeasebe.services.virtualAccounts;

import org.springframework.http.ResponseEntity;

public interface UserVirtualAccountService {
    ResponseEntity<?> generateUserVirtualAccountsService(String phoneNumber);
    ResponseEntity<?> deleteUserVirtualAccountsService(String userId);
    ResponseEntity<?> getUserVirtualAccountsService(String userId);
    ResponseEntity<?> getUserVirtualAccountByIdService(String userVirtualAccountId);
}
