package exam6.runner;

import java.util.concurrent.ExecutionException;

import exam6.classes.InvestingApp;

public class Runner {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		InvestingApp app = new InvestingApp();
		app.start();
	}

}
