package io.novocaine.example.service;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ThirdLevelService {

    @Inject
    private SecondLevelService secondLevelService;

    public SecondLevelService getSecondLevelService() {
        return secondLevelService;
    }
}
