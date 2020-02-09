package io.novocaine.example.service;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SecondLevelService {

    @Inject
    private LowLevelService lowLevelService;

    public LowLevelService getLowLevelService() {
        return lowLevelService;
    }
}
