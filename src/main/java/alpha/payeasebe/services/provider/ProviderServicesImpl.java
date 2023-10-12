package alpha.payeasebe.services.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import alpha.payeasebe.exceptions.custom.EntityFoundException;
import alpha.payeasebe.models.Providers;
import alpha.payeasebe.payloads.req.Providers.CreateProvidersRequest;
import alpha.payeasebe.payloads.res.ResponseHandler;
import alpha.payeasebe.repositories.ProviderRepository;

@Service
public class ProviderServicesImpl implements ProviderServices {
    @Autowired
    ProviderRepository providerRepository;

    @Override
    public ResponseEntity<?> createProviderService(CreateProvidersRequest request) {
        if (providerRepository.existsByName(request.getName())) {
            throw new EntityFoundException("Provider already exists");
        }

        Providers providers = new Providers(request.getName(), request.getProfilePicture());
        providerRepository.save(providers);

        return ResponseHandler.responseMessage(200, "Create Provider success!", true);
    }
}
