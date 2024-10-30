package ozdemir0ozdemir.javaconcurrency;

public class RaceCondition {


    static class MyRunnable implements Runnable {

        int count = 0;

        @Override
        public void run() {

            for (int i = 0; i < 1_000_000; i++) {
                synchronized (this) {
                    this.count++;
                }
            }
            System.out.println(STR."[\{Thread.currentThread().getName()}] : count = \{count}");
        }
    }

    public static void main(String[] args) throws InterruptedException {

        MyRunnable r1 = new MyRunnable();

        Thread t1 = new Thread(r1, "thread-1");
        Thread t2 = new Thread(r1, "thread-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println(STR."Finished final count: \{r1.count}");
    }
}
