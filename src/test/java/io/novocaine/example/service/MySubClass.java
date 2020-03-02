package io.novocaine.example.service;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MySubClass extends MyAbstractClass {

    private LowLevelService lowLevelService;

    @Inject
    public MySubClass(LowLevelService lowLevelService) {
        this.lowLevelService = lowLevelService;
    }

    public LowLevelService getLowLevelService() {
        return lowLevelService;
    }
}
