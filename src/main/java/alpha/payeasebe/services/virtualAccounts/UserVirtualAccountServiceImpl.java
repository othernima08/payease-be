package alpha.payeasebe.services.virtualAccounts;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import alpha.payeasebe.models.Providers;
import alpha.payeasebe.models.TransactionCategories;
import alpha.payeasebe.models.User;
import alpha.payeasebe.models.UserVirtualAccount;
import alpha.payeasebe.payloads.res.ResponseHandler;
import alpha.payeasebe.repositories.ProviderRepository;
import alpha.payeasebe.repositories.TransactionCategoryRepository;
import alpha.payeasebe.repositories.UserRepository;
import alpha.payeasebe.repositories.UserVirtualAccountRepository;
import alpha.payeasebe.validators.ProviderValidation;
import alpha.payeasebe.validators.TransactionCategoriesValidation;
import alpha.payeasebe.validators.UserValidation;

@Service
public class UserVirtualAccountServiceImpl implements UserVirtualAccountService {
    @Autowired
    UserVirtualAccountRepository userVirtualAccountRepository;

    @Autowired
    TransactionCategoryRepository transactionCategoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProviderRepository providerRepository;

    @Autowired
    ProviderValidation providerValidation;

    @Autowired
    UserValidation userValidation;

    @Autowired
    TransactionCategoriesValidation transactionCategoriesValidation;

    public static Boolean isValidProvider(String providerName) {
        return providerName.startsWith("Bank") || providerName.equals("Alfamart") || providerName.equals("Indomaret");
    }

    @Override
    public ResponseEntity<?> generateUserVirtualAccountsService(String phoneNumber) {
        String virtualAccountNumber = "";

        TransactionCategories transactionCategories = transactionCategoryRepository.findByType("Top Up");
        transactionCategoriesValidation.validateTransactionCategory(transactionCategories);

        User user = userRepository.findByPhoneNumber(phoneNumber);
        userValidation.validateUser(user);

        List<Providers> providers = providerRepository.findAll();

        List<Providers> filteredProviders = providers.stream()
                .filter(provider -> provider.getName().startsWith("Bank") || provider.getName().equals("Alfamart") || provider.getName().equals("Indomaret"))
                .collect(Collectors.toList());

        for (Providers providers2 : filteredProviders) {
            if (!providers2.getName().startsWith("Bank")) {
                virtualAccountNumber = phoneNumber;
            } else {
                if (providers2.getName() == "Bank BCA" || providers2.getName().equals("Bank BCA")) {
                    virtualAccountNumber = "3901" + phoneNumber;
                } else if (providers2.getName() == "Bank Mandiri" || providers2.getName().equals("Bank Mandiri")) {
                    virtualAccountNumber = "89508" + phoneNumber;
                } else if (providers2.getName() == "Bank BRI" || providers2.getName().equals("Bank BRI")) {
                    virtualAccountNumber = "88810" + phoneNumber;
                } else if (providers2.getName() == "Bank BNI" || providers2.getName().equals("Bank BNI")) {
                    virtualAccountNumber = "8810" + phoneNumber;
                }
            }
            UserVirtualAccount userVirtualAccount = new UserVirtualAccount(user, transactionCategories, providers2, virtualAccountNumber);    
            userVirtualAccountRepository.save(userVirtualAccount);
        }

        return ResponseHandler.responseMessage(200, "Generate virtual account for " + user.getFirstName() + " " + user.getLastName() + " success!", true);
    }

    @Override
    public ResponseEntity<?> getUserVirtualAccountsService(String phoneNumber) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserVirtualAccountsService'");
    }

}
