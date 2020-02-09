package io.novocaine.example.service;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FourthLevelService {

    @Inject
    private ThirdLevelService thirdLevelService;

    @Inject
    private SecondLevelService secondLevelService;

    public ThirdLevelService getThirdLevelService() {
        return thirdLevelService;
    }

    public SecondLevelService getSecondLevelService() {
        return secondLevelService;
    }
}
