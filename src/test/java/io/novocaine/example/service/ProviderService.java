package io.novocaine.example.service;

import javax.inject.Named;
import javax.inject.Singleton;

public class ProviderService {

    public class ProvidedService {

        private String name;

        public ProvidedService(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public class SecondProvidedService {

        private ProvidedService providedService;

        public SecondProvidedService(ProvidedService providedService) {
            this.providedService = providedService;
        }

        public ProvidedService getProvidedService() {
            return providedService;
        }
    }

    @Singleton
    public ProvidedService getProvidedService() {
        return new ProvidedService("providedService");
    }

    @Singleton
    public SecondProvidedService getSecondProvidedService(ProvidedService providedService) {
        return new SecondProvidedService(providedService);
    }

    @Singleton
    @Named("firstNamedProvidedService")
    public ProvidedService getFirstProvidedService() {
        return new ProvidedService("firstNamedProvidedService");
    }

    @Singleton
    @Named("secondNamedProvidedService")
    public ProvidedService getSecondProvidedService() {
        return new ProvidedService("secondNamedProvidedService");
    }
}
