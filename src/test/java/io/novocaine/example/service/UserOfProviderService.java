package io.novocaine.example.service;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class UserOfProviderService {

    @Inject
    private ProviderService providerService;

    @Inject
    private ProviderService.SecondProvidedService secondProvidedService;

    @Inject
    @Named("firstNamedProvidedService")
    private ProviderService.ProvidedService firstNamedProvidedService;

    @Inject
    @Named("secondNamedProvidedService")
    private ProviderService.ProvidedService secondNamedProvidedService;

    public ProviderService getProviderService() {
        return providerService;
    }

    public ProviderService.SecondProvidedService getSecondProvidedService() {
        return secondProvidedService;
    }

    public ProviderService.ProvidedService getFirstNamedProvidedService() {
        return firstNamedProvidedService;
    }

    public ProviderService.ProvidedService getSecondNamedProvidedService() {
        return secondNamedProvidedService;
    }
}
