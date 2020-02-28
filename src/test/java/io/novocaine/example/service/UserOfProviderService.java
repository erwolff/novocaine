package io.novocaine.example.service;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserOfProviderService {

    @Inject
    private ProviderService providerService;

    @Inject
    private ProviderService.SecondProvidedService secondProvidedService;

    public ProviderService getProviderService() {
        return providerService;
    }

    public ProviderService.SecondProvidedService getSecondProvidedService() {
        return secondProvidedService;
    }
}
