package alpha.payeasebe.services.provider;

import org.springframework.http.ResponseEntity;

import alpha.payeasebe.payloads.req.Providers.CreateProvidersRequest;

public interface ProviderServices {
    ResponseEntity<?> createProviderService(CreateProvidersRequest request);
}
