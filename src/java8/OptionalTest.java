package java8;

import java.util.Optional;

public class OptionalTest {
	
	public static void main(String[] args) {
		Person nullPerson = null;
		Person person = new Person();
		
		Optional<Person> personOptional = null;
		// personOptional = Optional.of(nullPerson);
		personOptional = Optional.empty();
		personOptional = Optional.ofNullable(nullPerson);
		personOptional = Optional.of(person);
		
		Optional<Car> carOptional = personOptional.map(Person::getCar);
		System.out.println(carOptional.orElse(null));
		System.out.println(carOptional.orElseGet(Car::new));
		
		Optional<String> name = personOptional.map(Person::getCar).map(Car::getInsurance).map(Insurance::getName);
		System.out.println(name);
		
		Insurance i = new Insurance();
		i.setName("name!");
		Car c = new Car();
		c.setInsurance(i);
		Person p = new Person();
		p.setCar(c);
		
		personOptional = Optional.of(p);
		name = personOptional.map(Person::getCar).map(Car::getInsurance).map(Insurance::getName);
		System.out.println(name);
		
		System.out.println(personOptional.map(pp -> {
			System.out.println("inner: " + pp.getCar());
			return pp.getCar();
		}));

		System.out.println("-----------");
		c.setInsurance(null);
		System.out.println(personOptional);
		System.out.println(personOptional.map(Person::getCar));
		System.out.println(personOptional.map(Person::getCar).map(Car::getInsurance));
		System.out.println(personOptional.map(Person::getCar).map(Car::getInsurance).map(Insurance::getName));
		System.out.println(personOptional.map(Person::getCar).map(Car::getInsurance).map(Insurance::getName).orElse("null!!"));
		System.out.println(personOptional.map(Person::getCar).map(Car::getInsurance).map(Insurance::getName).orElseGet(() -> "zzz"));
		System.out.println(personOptional.map(Person::getCar).map(Car::getInsurance).map(Insurance::getName).isPresent());
		personOptional.map(Person::getCar).map(Car::getInsurance).map(Insurance::getName).ifPresent(System.out::println);
	}
	
}

class Person {
	private Car car;
	public Car getCar() {
		return car;
	}
	public void setCar(Car car) {
		this.car = car;
	}
}

class Car {
	private Insurance insurance;
	public Insurance getInsurance() {
		return insurance;
	}
	public void setInsurance(Insurance insurance) {
		this.insurance = insurance;
	}
}

class Insurance {
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}