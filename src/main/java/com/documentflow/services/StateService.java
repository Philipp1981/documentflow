package com.documentflow.services;

import com.documentflow.entities.Department;
import com.documentflow.entities.State;
import com.documentflow.repositories.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class StateService {

    private StateRepository stateRepository;

    @Autowired
    public void setStateRepository(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    public State getStateById (int id) {
        return stateRepository.findOneById(id);
    }

    public State getStateByBusinessKey (String business_key) {
        return stateRepository.findOneByBusinessKey(business_key);
    }

    public List<State> findAllStates() {
        return stateRepository.findAll();
    }


}
