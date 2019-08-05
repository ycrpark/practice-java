package java8;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 탐색하려는 데이터를 포함하는 스트림을 어떻게 병렬화할 것인지 정의한다.
 */
public class SpliteratorTest {
	public static final String SENTENCE = " Nel   mezzo del cammin  di nostra  vita  mi  ritrovai in una  selva oscura  che la  dritta via era   smarrita ";
	
	/**
	 * 단어 개수 구하기 - 기존 순차 방식 계산
	 * @param s
	 * @return
	 */
	public static int countWords(String s) {
		int counter = 0;
		boolean lastSpace = true;
		for(char c : s.toCharArray()) {
			if(Character.isWhitespace(c)) {
				lastSpace = true;
			} else {
				if(lastSpace) {
					counter++;
				}
				lastSpace = false;
			}
		}
		return counter;
	}
	
	/**
	 * 단어 개수 구하기 - 함수형 계산
	 * @param stream
	 * @return
	 */
	private static int countWords(Stream<Character> stream) {
//		WordCounter wordCounter = stream.reduce(
//				new WordCounter(0, true),
//				(counter, c) -> counter.accumulate(c),
//				(c1, c2) -> c1.combine(c2)
//				);
		WordCounter wordCounter = stream.reduce(
				new WordCounter(0, true),
				WordCounter::accumulate,
				WordCounter::combine
				);
		
		return wordCounter.getCounter();
	}
	
	public static void main(String[] args) {
		// 기존 방식
		System.out.println("기존: " + countWords(SENTENCE));
		
		// IntStream test = IntStream.range(0, SENTENCE.length()).map(SENTENCE::charAt).parallel();
		
		// 함수형 방식
		Stream<Character> stream = IntStream.range(0, SENTENCE.length()).mapToObj(SENTENCE::charAt);
		System.out.println("함수형: " + countWords(stream));
		
		// parallel
		// 원래 문자열을 임의의 위치에서 둘로 나누다보니 예상치 못하게 하나의 단어를 둘로 계산하는 상황이 발생할 수 있다.
		// 즉, 순차 스트림을 병렬 스트림으로 바꿀 때 스트림 분할 위치에 따라 잘못된 결과가 나올 수 있다.
		// 문자열을 임의의 위치에서 분할하지 말고 단어가 끝나는 위치에서만 분할하는 방법으로 이 문제를 해결할 수 있다.
		stream = IntStream.range(0, SENTENCE.length()).mapToObj(SENTENCE::charAt).parallel();
		System.out.println("함수형 병렬: " + countWords(stream));
		
		// spliterator parallel
		Spliterator<Character> spliterator = new WordCounterSpliterator(SENTENCE);
		stream = StreamSupport.stream(spliterator, true);
		System.out.println("spliterator 사용: " + countWords(stream));
	}
	
}

/**
 * reduce 계산용 identity
 */
class WordCounter {
	private final int counter;
	private final boolean lastSpace;
	
	public WordCounter(int counter, boolean lastSpace) {
		this.counter = counter;
		this.lastSpace = lastSpace;
	}
	
	/**
	 * stream의로 계산하기위해 변환
	 * @param c
	 * @return
	 */
	public WordCounter accumulate(Character c) {
		if(Character.isWhitespace(c)) {
			return lastSpace ? this : new WordCounter(counter, true);
		} else {
			return lastSpace ? new WordCounter(counter + 1, false) : this;
		}
	}
	
	/**
	 * 합치기
	 * @param wordCounter
	 * @return
	 */
	public WordCounter combine(WordCounter wordCounter) {
		return new WordCounter(counter + wordCounter.counter, wordCounter.lastSpace);
	}
	
	public int getCounter() {
		return counter;
	}
}

/**
 * 병렬 스트림 계산용 - 단어 끝나는 시점에 분할
 */
class WordCounterSpliterator implements Spliterator<Character> {
	
	private final String string;
	private int currentChar = 0;
	
	public WordCounterSpliterator(String string) {
		this.string = string;
	}
	
	/**
	 * consumer 수행 후 인덱스 증가
	 * @return 진행가능 인덱스 남았는지 여부
	 */
	@Override
	public boolean tryAdvance(Consumer<? super Character> action) {
		action.accept(string.charAt(currentChar++));
		return currentChar < string.length();
	}
	
	/**
	 * 병렬처리 분할하는 로직
	 * @return 분할 종료 시 null
	 */
	@Override
	public Spliterator<Character> trySplit() {
		int currentSize = string.length() - currentChar;
		// 최대 10글자
		if(currentSize < 10) {
			return null;
		}
		
		// 남은 문자열의 중간지점부터 시작
		for(int splitPos = currentSize / 2 + currentChar; splitPos < string.length(); splitPos++) {
			// 다음 공백이 나오면 -> 단어 끝나면
			if(Character.isWhitespace(string.charAt(splitPos))) {
				// 처음부터 분할지점까지
				Spliterator<Character> spliterator = new WordCounterSpliterator(string.substring(currentChar, splitPos));
				// 새로운 시작 위치
				currentChar = splitPos;
				return spliterator;
			}
		}
		return null;
	}
	
	/**
	 * 탐색해야 할 요소의 개수
	 */
	@Override
	public long estimateSize() {
		return string.length() - currentChar;
	}
	
	/**
	 * 특성
	 * ORDERED : 문자열의 문자 등장 순서가 유의미함
	 * SIZED : estimatedSize 메서드의 반환값이 정확함
	 * SUBSIZED : trySplit으로 생성된 Spliterator도 정확한 크기를 가짐
	 * NOTNULL : 문자열에는 null 문자가 존재하지 않음
	 * IMMUTABLE : 문자열 자체가 불변 클래스이므로 문자열을 파싱하면서 속성이 추가되지 않음
	 */
	@Override
	public int characteristics() {
		return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
	}
}
