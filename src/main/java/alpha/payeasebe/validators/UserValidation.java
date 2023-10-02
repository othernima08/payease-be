package alpha.payeasebe.validators;

import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.stereotype.Service;

import alpha.payeasebe.models.User;

@Service
public class UserValidation {
    public void validateUser(User user) {
        if(user == null || Objects.isNull(user)){
            throw new NoSuchElementException("User is not found");
        }
    }
}
