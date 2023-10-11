package alpha.payeasebe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import alpha.payeasebe.services.virtualAccounts.UserVirtualAccountService;

@RestController
@RequestMapping("/virtual-accounts")
public class UserVirtualAccountController {
    @Autowired
    UserVirtualAccountService userVirtualAccountService;

    @PostMapping()
    public ResponseEntity<?> registerService(@RequestParam String phoneNumber) {
        return userVirtualAccountService.generateUserVirtualAccountsService(phoneNumber);
    }
}
