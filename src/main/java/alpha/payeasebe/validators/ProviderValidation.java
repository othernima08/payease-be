package alpha.payeasebe.validators;

import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.stereotype.Service;

import alpha.payeasebe.models.Providers;

@Service
public class ProviderValidation {
    public void validateProvider(Providers providers) {
        if(providers == null || Objects.isNull(providers)){
            throw new NoSuchElementException("Providers is not found");
        }
    }
}
