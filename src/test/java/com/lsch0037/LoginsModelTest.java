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

public class LoginsModelTest implements FsmModel {
    Runner sut = new Runner(Constants.userId, Constants.driverPath);

    LoginStatesEnum statesEnum = LoginStatesEnum.LoggedOut;

    @Override
    public LoginStatesEnum getState(){
        return statesEnum;
    }

    @Override
    public void reset(boolean testing){
        if(testing){
            sut = new Runner(Constants.userId, Constants.driverPath);
        }
        sut.goToLoginPage();

        statesEnum = LoginStatesEnum.LoggedOut;
    }

    public boolean LoginGuard(){return getState().equals(LoginStatesEnum.LoggedOut);}
    public @Action void Login(){
        sut.goToLoginPage();
        sut.goodLogin();

        statesEnum = LoginStatesEnum.LoggedIn;

        Assert.assertTrue(sut.getLoginStatus());
    }

    public boolean LogoutGuard(){return getState().equals(LoginStatesEnum.LoggedIn);}
    public @Action void Logout(){
        sut.logout();

        statesEnum = LoginStatesEnum.LoggedOut;

        Assert.assertFalse(sut.getLoginStatus());
    }

    //Test runner
	@Test
	public void LoginsModelRunner() {
		final GreedyTester tester = new GreedyTester(new LoginsModelTest()); //Creates a test generator that can generate random walks. A greedy random walk gives preference to transitions that have never been taken before. Once all transitions out of a state have been taken, it behaves the same as a random walk.
		tester.setRandom(new Random()); //Allows for a random path each time the model is run.
		tester.buildGraph(); //Builds a model of our FSM to ensure that the coverage metrics are correct.
		// tester.addListener(new StopOnFailureListener()); //This listener forces the test class to stop running as soon as a failure is encountered in the model.
		tester.addListener("verbose"); //This gives you printed statements of the transitions being performed along with the source and destination states.
		tester.addCoverageMetric(new TransitionPairCoverage()); //Records the transition pair coverage i.e. the number of paired transitions traversed during the execution of the test.
		tester.addCoverageMetric(new StateCoverage()); //Records the state coverage i.e. the number of states which have been visited during the execution of the test.
		tester.addCoverageMetric(new ActionCoverage()); //Records the number of @Action methods which have ben executed during the execution of the test.
		// tester.generate(500); //Generates 500 transitions
        tester.generate(20);
		tester.printCoverage(); //Prints the coverage metrics specified above.
	}    
}
