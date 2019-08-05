package java8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;


public class StreamTest {
	
	public static void main(String[] args) {
		List<Integer> nums = new ArrayList<Integer>();
		nums.add(4);
		nums.add(2);
		
		// Stream.of
		Stream.of(1, 2, 3, 6, 4, 9).map(Integer::intValue).forEach(System.out::println);
		int[] arr = {1, 2, 3};
		System.out.println(Arrays.stream(arr).sum());
		
		// IntStream
		System.out.println(IntStream.iterate(0, n -> ++n).limit(11).sum());
		
		// iterate
		Stream.iterate(new int[]{0, 1}, t -> new int[]{t[1], t[0] + t[1]})
		.limit(10)
		.forEach(t -> System.out.println("(" + t[0] + ", " + t[1] + ")"));
		
		Stream.iterate(new int[]{0, 1}, t -> new int[]{t[1], t[0] + t[1]})
		.limit(10)
		.map(t -> t[0])
		.forEach(System.out::println);
		
		// generate
		Stream.generate(Math::random).limit(11).forEach(System.out::println);
		Stream.of(1, 2, 3, 6, 4, 9, null, null).distinct().forEach(System.out::println);
		IntStream.of(1, 2, 3).distinct().forEach(System.out::println);
		
		nums.stream().filter(n -> n == null ? false : n > 0).collect(Collectors.toList());

		// terminal operation
		// collect, anyMatch, noneMatch, allMatch, findAny, findFirst, forEach, reduce, count
		System.out.println("------------------------------------");
		List<String> words = Arrays.asList("Hello", "World");
		words.stream().map(word -> word.split("")).distinct().forEach(System.out::println);
		words.stream().flatMap(line -> Arrays.stream(line.split(""))).distinct().forEach(System.out::println);
		System.out.println(words.stream().anyMatch(z -> z.length() > 3));
		System.out.println(words.stream().reduce("zz", (aa, b) -> aa + b));
		System.out.println(nums.stream().collect(Collectors.maxBy((n1, n2) -> (n1 == null ? new Integer(0) : n1).compareTo(n2))).get());
		System.out.println(nums.stream().collect(Collectors.summingInt(n -> n)));
		System.out.println(nums.stream().collect(Collectors.averagingInt(n -> n)));
		System.out.println(nums.stream().collect(Collectors.summarizingInt(n -> n)));
		System.out.println(nums.stream().map(n -> n.toString()).collect(Collectors.joining(",")));
		System.out.println(nums.stream().reduce(0, (n1, n2) -> n1 + n2));
		System.out.println(nums.stream().collect(Collectors.reducing(0, n -> n + 1, (n1, n2) -> n1 + n2)));
		System.out.println(nums.stream().reduce(0, (n1, n2) -> n1 + n2 + 10, (n1, n2) -> n1 + n2));
		System.out.println(nums.stream().mapToInt(Integer::intValue).average().orElseGet(null));
		System.out.println(IntStream.rangeClosed(0, 100).count());
		System.out.println(nums.stream().mapToInt(Integer::intValue).max().orElse(44));
		System.out.println(nums.stream().mapToInt(Integer::intValue).boxed().mapToInt(Integer::intValue).boxed().reduce((n1, n2) -> n1 + n2).get());

		// groupingBy
		System.out.println(nums.stream().collect(Collectors.groupingBy(n -> n / 2, Collectors.groupingBy(n -> n / 2))));
		System.out.println(nums.stream().collect(Collectors.groupingBy(n -> n / 2, Collectors.toList())));
		List<Dish> dishes = new ArrayList<Dish>();
		Dish d = new Dish();
		d.setType(1);
		dishes.add(d);
		d = new Dish();
		d.setType(2);
		dishes.add(d);
		d = new Dish();
		d.setType(2);
		dishes.add(d);
		d = new Dish();
		d.setType(3);
		dishes.add(d);
		
		System.out.println(dishes.stream().collect(Collectors.groupingBy(dish -> dish.getType())));
		System.out.println(dishes.stream().collect(Collectors.groupingBy(dish -> dish.getType(), Collectors.reducing((d1, d2) -> d1))));
		System.out.println(dishes.stream().collect(
				Collectors.groupingBy(
						dish -> dish.getType(),
						Collectors.collectingAndThen(Collectors.reducing((d1, d2) -> d1),
								Optional::get))));
		System.out.println(dishes.stream().collect(Collectors.partitioningBy(dish -> dish.getType() == 1)));
		
		// parallel sequential
		System.out.println(Stream.iterate(0L, i -> ++i).sequential().parallel().limit(10).reduce(Long::sum).get());
		
		System.out.println(LongStream.rangeClosed(1, 100).sum());
		System.out.println(LongStream.rangeClosed(1, 100).reduce(Long::sum).getAsLong());
		LongStream.rangeClosed(0, 100).parallel().sum();
		Accumulator accumulator = new Accumulator();
		LongStream.rangeClosed(1, 100).parallel().forEach(accumulator::add);
		System.out.println(accumulator.total);
		nums.add(1);
		nums.add(5);
		nums.add(7);
		System.out.println(nums);
		System.out.println(nums.stream().limit(3).collect(Collectors.toList()));
		// unordered
		System.out.println(nums.stream().unordered().unordered().collect(Collectors.toList()));
	}
	
}

class Dish {
	int type;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "type:" + type;
	}
}

class Accumulator {
	long total = 0;
	
	public void add(long value) {
		total += value;
	}
}
