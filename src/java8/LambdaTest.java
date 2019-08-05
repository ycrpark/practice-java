package java8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class LambdaTest {
	
	public static void main(String[] args) {
		List<Integer> nums = new ArrayList<Integer>();
//		nums.add(null);
		nums.add(4);
		nums.add(2);
		
		nums.stream().filter(num -> num > 2);
		System.out.println(nums);
		
		List<String> str = Arrays.asList("a", "b", "A", "B");
		str.sort(String::compareToIgnoreCase);
		
		Function<String, Integer> f = (s) -> s.length();
		String a = "3";
		Supplier<String> s = String::new;
		System.out.println(333);
		System.out.println(s.get());
		String aaa = new String();
		
		Collections.sort(nums, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				System.out.println(a);
				return 0;
			}
		});
		// 함수형 인터페이스를 람다식으로
		Collections.sort(nums, (n1, n2) -> n1.compareTo(n2));
		
		Comparator<Integer> cc = new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				System.out.println(a);
				return 0;
			}
		};
		cc = (o1, o2) -> 0;
		// 메소드 레퍼런스
		cc = Integer::compareTo;
		
		Collections.sort(nums, Integer::compareTo);
		nums.forEach((n) -> System.out.println(n));
	}
	
}
