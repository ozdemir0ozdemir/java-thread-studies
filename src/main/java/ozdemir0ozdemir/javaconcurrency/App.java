package ozdemir0ozdemir.javaconcurrency;


import java.util.ArrayList;
import java.util.List;

public class App {

    static class Car {}
    static class Truck extends Car {}

    public static void main(String[] args) {

//        Truck truck = (Truck) new Car();
        Car car = new Truck();

        List<Car> cars = new ArrayList<>();
        cars.add(car);
        cars.add(new Truck());

    }
}
