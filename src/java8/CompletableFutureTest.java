package java8;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CompletableFutureTest {
	
	public static void main(String[] args) {
		// completableFuture
		CompletableFuture<Double> completableFuture = new CompletableFuture<Double>();
		new Thread(() -> {
			try {
				double price = getTen();
				completableFuture.complete(price);
			} catch(Exception e) {
				completableFuture.completeExceptionally(e);
			}
		}).start();
		
		try {
			System.out.println(completableFuture.get());
			
			// supplyAsync
			System.out.println(CompletableFuture.supplyAsync(CompletableFutureTest::getTen).get());
			
			// runAsync
			System.out.println(CompletableFuture.runAsync(CompletableFutureTest::getTen).get());
			
			// thenApply
			System.out.println(CompletableFuture.supplyAsync(CompletableFutureTest::getTen)
					.thenApply(n -> ++n)
					.thenApply(n -> ++n)
					.thenApply(n -> ++n)
					.get());
			
			// thenAccept
			System.out.println(CompletableFuture.supplyAsync(CompletableFutureTest::getTen)
					.thenApply(n -> ++n).thenAccept(n -> {})
					.get());
			
			// thenRun
			System.out.println(CompletableFuture.supplyAsync(CompletableFutureTest::getTen)
					.thenApply(n -> ++n).thenRun(() -> {})
					.get());
			
			// thenCompose
			System.out.println(CompletableFuture.supplyAsync(CompletableFutureTest::getTen)
					.thenApply(n -> ++n)
					.thenCompose(n -> CompletableFuture.supplyAsync(() -> getTen()))
					.get());
			
			// thenCombine
			System.out.println(CompletableFuture.supplyAsync(CompletableFutureTest::getTen)
					.thenApply(n -> ++n)
					.thenCombine(CompletableFuture.supplyAsync(CompletableFutureTest::getTen), (n1, n2) -> n1 + n2)
					.get());
			
			// thenAcceptBoth
			System.out.println(CompletableFuture.supplyAsync(CompletableFutureTest::getTen)
					.thenApply(n -> ++n)
					.thenAcceptBoth(CompletableFuture.supplyAsync(CompletableFutureTest::getTen), (n1, n2) -> {})
					.get());
			
		} catch(InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		List<String> names = new ArrayList<>();
		names.add("");
		names.add("a");
		names.add("aaaaa");
		names.add("aaaa");
		names.add("aaaaaa");
		names.add("aaa");
		names.add("aaaaaaa");
		names.add("aaaaaaaaaaaaaa");
		names.add("aaaaaaaaaa");
		names.add("aaaaaaaaa");
		
		// completableFutures stream
		long start = System.currentTimeMillis();
		List<CompletableFuture<Integer>> futures = names.stream()
				.map(name -> CompletableFuture.supplyAsync(() -> getLength(name)))
				.collect(Collectors.toList());
		
		List<Integer> lengths = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		System.out.println(lengths);
		System.out.println(System.currentTimeMillis() - start);
		
		// custom executor
		Executor executor = Executors.newFixedThreadPool(names.size(), (runnable) -> {
			Thread thread = new Thread(runnable);
			thread.setDaemon(true);
			return thread;
		});
		
		start = System.currentTimeMillis();
		futures = names.stream()
				.map(name -> CompletableFuture.supplyAsync(() -> getLength(name), executor))
				.collect(Collectors.toList());
		
		lengths = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		System.out.println(lengths);
		System.out.println(System.currentTimeMillis() - start);
		
		// completableFuture in stream
		futures = names.stream()
		.map(name -> CompletableFuture.supplyAsync(() -> getLength(name), executor))
		// thenApply
		.map(future -> future.thenApply(len -> {
			System.out.println(len);
			return len;
		}))
		// thenCompose
		.map(future -> future.thenCompose(len -> CompletableFuture.supplyAsync(() -> len + 100, executor)))
		.collect(Collectors.toList());
		
		lengths = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		System.out.println(lengths);
	}
	
	private static double getTen() {
		try {
			Thread.sleep(1000);
		} catch(InterruptedException e) {
		}
		return 10;
	}
	private static int getLength(String name) {
		try {
			Thread.sleep(1000);
			return name.length();
		} catch(InterruptedException e) {
			return 0;
		}
	}
}
