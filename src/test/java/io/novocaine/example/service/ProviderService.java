package io.novocaine.example.service;

import javax.inject.Singleton;

public class ProviderService {

    public class ProvidedService {

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
        return new ProvidedService();
    }

    @Singleton
    public SecondProvidedService getSecondProvidedService(ProvidedService providedService) {
        return new SecondProvidedService(providedService);
    }
}
