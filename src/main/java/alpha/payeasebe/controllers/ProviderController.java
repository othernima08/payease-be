package alpha.payeasebe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import alpha.payeasebe.payloads.req.Providers.CreateProvidersRequest;
import alpha.payeasebe.services.provider.ProviderServices;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/providers")
public class ProviderController {
    @Autowired
    ProviderServices providerServices;

    @PostMapping()
    public ResponseEntity<?> createProvider(@RequestBody @Valid CreateProvidersRequest request) {
        return providerServices.createProviderService(request);
    }
}
