package alpha.payeasebe.validators;

import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.stereotype.Service;

import alpha.payeasebe.models.TopUp;

@Service
public class TopUpValidation {
    public void validateTopUp(TopUp topUp) {
        if(topUp == null || Objects.isNull(topUp)){
            throw new NoSuchElementException("Top up is not found");
        }
    }
}
