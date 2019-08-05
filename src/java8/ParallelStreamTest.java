package java8;

import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class ParallelStreamTest {
	public static long iterativeSum(long n) {
		long result = 0;
		for(long i = 1L; i <= n; i++) {
			result += i;
		}
		return result;
	}
	
	public static long sequentialSum(long n) {
		return Stream.iterate(1L, i -> i + 1) // 무한 자연수 스트림 생성
				.limit(n) // n 개 이하로 제한
				.reduce(0L, Long::sum); // 모든 숫자를 더하는 스트림 리듀싱 연산
	}
	
	public static long parallelSum(long n) {
		return Stream.iterate(1L, i -> i + 1)
				.limit(n)
				.parallel() // 스트림을 병렬 스트림으로 변환
				.reduce(0L, Long::sum);
	}
	
	public static long measureSumPerf(Function<Long, Long> adder, long n) {
		long fastest = Long.MAX_VALUE;
		for(int i = 0; i < 10; i++) {
			long start = System.nanoTime();
			long sum = adder.apply(n);
			long duration = (System.nanoTime() - start) / 1_000_000;
			//System.out.println("Result: " + sum);
			if(duration < fastest)
				fastest = duration;
		}
		return fastest;
	}
	
	public static long rangedSum(long n) {
		return LongStream.rangeClosed(1, n).reduce(0L, Long::sum);
	}
	
	public static long parallelRangedSum(long n) {
		return LongStream.rangeClosed(1, n).parallel().reduce(0L, Long::sum);
	}
	
	public static long sideEffectSum(long n) {
		// 공유 자원 병렬 처리 불가
		Accumulator accumulator = new Accumulator();
		LongStream.rangeClosed(1, n).forEach(accumulator::add);
		return accumulator.total;
	}
	
	public static void main(String[] args) {
		IntStream.rangeClosed(1, 100).filter(n -> n > 5).sequential()
				.map(n -> n).parallel().reduce(Integer::sum);
		
		// ForkJoinPool의 스래드 수
		System.out.println(Runtime.getRuntime().availableProcessors());
		System.out.println(System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism"));
		
		// iterativeSum
		System.out.println(measureSumPerf(ParallelStreamTest::iterativeSum, 10000000));
		// sequentialSum
		System.out.println(measureSumPerf(ParallelStreamTest::sequentialSum, 10000000));
		// parallelSum
		// iterate는 병렬로 실행될 수 있도록 독립적인 청크로 분할하기가 어렵다.
		// 이전 연산의 결과에 따라 다음 함수의 입력이 달라지기 때문에 iterate연산을 청크로 분할하기가 어렵다.
		System.out.println(measureSumPerf(ParallelStreamTest::parallelSum, 10000000));
		// rangeClosed
		// 박싱, 언박싱 과정 없음
		System.out.println(measureSumPerf(ParallelStreamTest::rangedSum, 10000000));
		// parallel rangeClosed
		System.out.println(measureSumPerf(ParallelStreamTest::parallelRangedSum, 10000000));
		
	}
	
}

