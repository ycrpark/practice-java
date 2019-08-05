package java8;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

/**
 * 스트림 내부적으로 처리되는 forkjoin framework
 * since java 7
 * 병렬화 할 수 있는 태스크를 작은 태스크로 분할한 다음에 분할된 태스크를 각각의 스레드로 실행하여 서브태스크 각각의 결과를 합쳐서 최종 결과를 생산한다.
 */
public class ForkJoinTest extends RecursiveTask<Long> {
	private static final long serialVersionUID = -7299971340974343355L;
	
	public static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();
	
	public static final long THRESHOLD = 1000;
	
	private final long[] numbers;
	private final int start;
	private final int end;
	
	public ForkJoinTest(long[] numbers) {
		this(numbers, 0, numbers.length);
	}
	
	private ForkJoinTest(long[] numbers, int start, int end) {
		this.numbers = numbers;
		this.start = start;
		this.end = end;
	}
	
	@Override
	protected Long compute() {
//		if(태스크가 충분히 작거나 더 이상 분할할 수 없으면) {
//			순차적으로 태스크 계산
//		} else {
//			태스크를 두 서브태스크로 분할
//			태스크가 다시 서브태스크로 분할되도록 이 메서드를 재귀적으로 호출함
//			모든 서브태스크의 연산이 완료될 때까지 기다림
//			각 서브태스크의 결과를 합침
//		}

		int length = end - start;
		if(length <= THRESHOLD) {
			return computeSequentially();
		}
		ForkJoinTest leftTask = new ForkJoinTest(numbers, start, start + length / 2);
		leftTask.fork();
		ForkJoinTest rightTask = new ForkJoinTest(numbers, start + length / 2, end);
		// 둘다 fork하는 것보다 효율
		// 불필요 테스크 할당하는 오버헤드 피할 수 있음
		// 스레드 재 사용 가능
		Long rightResult = rightTask.compute();
		Long leftResult = leftTask.join();
		return leftResult + rightResult;
	}
	
	private long computeSequentially() {
		long sum = 0;
		for(int i = start; i < end; i++) {
			sum += numbers[i];
		}
		return sum;
	}
	
	public static void main(String[] args) {
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		long[] numbers = LongStream.rangeClosed(1, 100000).toArray();
		ForkJoinTask<Long> task = new ForkJoinTest(numbers);
		System.out.println(FORK_JOIN_POOL.invoke(task));
	}
	
}
