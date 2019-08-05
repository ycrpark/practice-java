package java8;

public class DefaultMethodTest {
	
	public static void main(String[] args) {
		new C().runDefault();
		// 시그니처 충돌
		new CC().run();
		// 상속 우선
		new EE().run();
		new FF().run();
	}
	
}

interface A {
	public void runA();
}
interface B extends A {
	public void runB();
	// 추가 구현 없이 추가 가능
	public default void runDefault() {
		System.out.println("default");
	}
}
class C implements B {
	@Override
	public void runA() {
	}
	@Override
	public void runB() {
	}
}

interface AA {
	public default void run() {
		System.out.println("AA");
	}
}
interface BB {
	public default void run() {
		System.out.println("BB");
	}
}
class CC implements AA, BB {
	@Override
	public void run() {
		// 시그니처 충돌 시 명시해야 함
		AA.super.run();
	}
}
class DD {
	public void run() {
		System.out.println("DD");
	}
}
class EE extends DD implements AA, BB {
	// 상속 우선
}
class FF implements AA {
	
}