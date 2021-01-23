package com.vmware.numbergenerator.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/*
 * NumberGenerator Bean
 */
public class NumberGenerator {

    @NotNull
    @NotEmpty(message = "Goal is mandatory")
    @Positive
    private String goal;
    @NotNull
    @NotEmpty(message = "Step is mandatory")
    @Positive
    private String step;

    public NumberGenerator() {}

    public NumberGenerator(String goal, String step) {
        this.goal = goal;
        this.step = step;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }
}
