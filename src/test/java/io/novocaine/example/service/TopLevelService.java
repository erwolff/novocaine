package io.novocaine.example.service;

import io.novocaine.example.service.FifthLevelService;

import javax.inject.Inject;

public class TopLevelService {

    @Inject
    private FifthLevelService fifthLevelService;

    public FifthLevelService getFifthLevelService() {
        return fifthLevelService;
    }
}
