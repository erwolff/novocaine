package io.novocaine.example.service;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FifthLevelService {

    @Inject
    private FourthLevelService fourthLevelService;

    public FourthLevelService getFourthLevelService() {
        return fourthLevelService;
    }
}
