package io.novocaine.example.service;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MyCyclicDependencyServiceOne {

    /*private final MyCyclicDependencyServiceTwo myCyclicDependencyServiceTwo;

    @Inject
    public MyCyclicDependencyServiceOne(MyCyclicDependencyServiceTwo myCyclicDependencyServiceTwo) {
        this.myCyclicDependencyServiceTwo = myCyclicDependencyServiceTwo;
    }*/
}
