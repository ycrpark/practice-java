package java8;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FutureTest {
	
	public static void main(String[] args) {
		ExecutorService executorService = Executors.newCachedThreadPool();
		
//		Future<Double> future = executorService.submit(new Callable<Double>() {
//			@Override
//			public Double call() throws Exception {
//				return doComputation();
//			}
//		});
		
		Future<Double> future = executorService.submit(FutureTest::doComputation);
		
		try {
			System.out.println(future.isDone());
			System.out.println(future.get(1, TimeUnit.SECONDS));
			System.out.println(future.isDone());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static double doComputation() {
		try {
			Thread.sleep(1000);
		} catch(InterruptedException e) {
		}
		return 10;
	}
}
