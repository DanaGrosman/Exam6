package exam6.classes;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import exam6.classes.Command.Operation;

public class InvestingApp {

	private final int COMMANDS_SIZE = 10;
	private final double BUY_AMOUNT = 0.02;
	private final double SELL_AMOUNT = 0.01;

	List<Command> commands = new ArrayList<Command>(COMMANDS_SIZE);
	private double totalFunds = 0;

	public void start() throws InterruptedException, ExecutionException {
		printStocks();
		initCommands();
		System.out.println("Total funds: " + totalFunds);
		System.out.println("------------------");
		System.out.println("After commands: ");
		handleCommands();
		printStocks();
	}

	private void handleCommands() throws InterruptedException, ExecutionException {
		ExecutorService executorService = Executors.newCachedThreadPool();
		List<Callable<Double>> callables = new ArrayList<>();

		for (Command command : commands) {
			callables.add(() -> {
				double funds = 0;
				try {
					Stock stock = StocksDB.getStockByName(command.stockName);

					if (command.operation == Operation.BUY) {
						funds = stock.getBuyPrice();
						stock.setBuyPrice(funds + BUY_AMOUNT);
					} else {
						funds = stock.getSellPrice();
						stock.setSellPrice(funds - SELL_AMOUNT);
					}
				} catch (IllegalArgumentException e) {
					System.err.println("Stock \"" + command.stockName + "\" is not found in DB");
				}
				return funds;
			});
		}

		List<Future<Double>> futures = executorService.invokeAll(callables);
		for (Future<Double> future : futures) {
			totalFunds += future.get();
		}

		System.out.println("The total funds is: " + totalFunds);

		executorService.shutdown();
		executorService.awaitTermination(2, TimeUnit.SECONDS);
	}

	private void initCommands() {
		commands.add(new Command("doodle", Operation.BUY));
		commands.add(new Command("doodle", Operation.SELL));
		commands.add(new Command("doodle", Operation.BUY));
		commands.add(new Command("HEADBOOK", Operation.BUY));
		commands.add(new Command("HEADBOOK", Operation.SELL));
		commands.add(new Command("HEADBOOK", Operation.SELL));
		commands.add(new Command("BARVAZON", Operation.BUY));
		commands.add(new Command("dana", Operation.SELL));
		commands.add(new Command("BARVAZON", Operation.BUY));
		commands.add(new Command("BARVAZON", Operation.BUY));
	}

	private void printStocks() {
		StocksDB.getStocks().entrySet().forEach(entry -> {
			System.out.println(entry.getKey() + " " + entry.getValue());
		});
	}

}
