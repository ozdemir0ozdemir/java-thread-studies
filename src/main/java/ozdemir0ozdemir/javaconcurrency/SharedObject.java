package ozdemir0ozdemir.javaconcurrency;

public class SharedObject {

    static class MyObject {}

    static class MyRunnable implements Runnable {

        private MyObject myObject;

        MyRunnable() {
            myObject = new MyObject();
        }
        MyRunnable(MyObject myObject) {
            this.myObject = myObject;
        }

        @Override
        public void run() {
            int count = 0;

            for(int i = 0; i < 1_000_0000; i++) {
                count++;
            }

            System.out.println(STR."[\{Thread.currentThread().getName()}] : myObject = \{myObject}");
            System.out.println(STR."[\{Thread.currentThread().getName()}] : count = \{count}");
        }
    }

    public static void main(String[] args) throws InterruptedException {

        MyObject myObject = new MyObject();

        Runnable r1 = new MyRunnable(myObject);
        Runnable r2 = new MyRunnable(myObject);

        Thread t1 = new Thread(r1, "thread-1");
        Thread t2 = new Thread(r2, "thread-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Finished");
    }
}
