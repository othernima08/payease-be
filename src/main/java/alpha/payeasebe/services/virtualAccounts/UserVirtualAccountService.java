package alpha.payeasebe.services.virtualAccounts;

import org.springframework.http.ResponseEntity;

public interface UserVirtualAccountService {
    ResponseEntity<?> generateUserVirtualAccountsService(String phoneNumber);
    ResponseEntity<?> getUserVirtualAccountsService(String phoneNumber);
}
