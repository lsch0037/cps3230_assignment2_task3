package com.lsch0037;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import nz.ac.waikato.modeljunit.Action;
import nz.ac.waikato.modeljunit.FsmModel;
import nz.ac.waikato.modeljunit.GreedyTester;
import nz.ac.waikato.modeljunit.StopOnFailureListener;
import nz.ac.waikato.modeljunit.coverage.ActionCoverage;
import nz.ac.waikato.modeljunit.coverage.StateCoverage;
import nz.ac.waikato.modeljunit.coverage.TransitionPairCoverage;

public class AlertsModelTest implements FsmModel {
    Runner sut = new Runner(Constants.userId, Constants.driverPath);

    AlertsStatesEnum statesEnum = AlertsStatesEnum.Normal;

    int alerts = 0;

    @Override
    public AlertsStatesEnum getState(){
        return statesEnum;
    }

    @Override
    public void reset(boolean testing){
        if(testing){
            sut = new Runner(Constants.userId, Constants.driverPath);
        }

        sut.deleteAlerts();
        alerts = 0;
        statesEnum = AlertsStatesEnum.Normal;
    }

    public boolean CreateAlertGuard(){return getState().equals(AlertsStatesEnum.Normal);}
    public @Action void CreateAlert(){
        sut.postValidAlert();

        alerts++;
        
        Assert.assertEquals(sut.getAlerts(), alerts);
    }

    public boolean DeleteAlertsGuard(){return getState().equals(AlertsStatesEnum.Normal);}
    public @Action void deleteAlerts(){
        sut.deleteAlerts();

        alerts = 0;
        
        Assert.assertEquals(sut.getAlerts(), alerts);
    }
    
    //this code is borrowed from the cps3230 Model-Based Testing tutorial
    //Test runner
	@Test
	public void AlertsModelRunner() {
		final GreedyTester tester = new GreedyTester(new AlertsModelTest()); //Creates a test generator that can generate random walks. A greedy random walk gives preference to transitions that have never been taken before. Once all transitions out of a state have been taken, it behaves the same as a random walk.
		tester.setRandom(new Random()); //Allows for a random path each time the model is run.
		tester.buildGraph(); //Builds a model of our FSM to ensure that the coverage metrics are correct.
		// tester.addListener(new StopOnFailureListener()); //This listener forces the test class to stop running as soon as a failure is encountered in the model.
		tester.addListener("verbose"); //This gives you printed statements of the transitions being performed along with the source and destination states.
		tester.addCoverageMetric(new TransitionPairCoverage()); //Records the transition pair coverage i.e. the number of paired transitions traversed during the execution of the test.
		tester.addCoverageMetric(new StateCoverage()); //Records the state coverage i.e. the number of states which have been visited during the execution of the test.
		tester.addCoverageMetric(new ActionCoverage()); //Records the number of @Action methods which have ben executed during the execution of the test.
		tester.generate(20); //Generates 500 transitions
		tester.printCoverage(); //Prints the coverage metrics specified above.
	}    
}
