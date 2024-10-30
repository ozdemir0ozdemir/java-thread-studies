package ozdemir0ozdemir.javaconcurrency.threadpool;

import java.util.*;
import java.util.stream.IntStream;

public class ThreadPool01 {
    private static final Random random = new Random();

    private static Runnable generateRunnable(int taskNo, int origin, int bounds) {
        return () -> {
            int duration;
            if(origin == bounds){
                duration = origin;
            } else {
              duration = random.nextInt(origin, bounds);
            }
            String message = STR."\{Thread.currentThread().getName()}: Task \{taskNo} and duration: \{duration}";
            System.out.println(message);
            try {
                Thread.sleep(duration);
            } catch (InterruptedException ex) {
            }
        };
    }

    void main() throws InterruptedException {

        ThreadPool threadPool = new ThreadPool(5, 50, "MyPool");

        IntStream.range(0, 100)
                        .forEach(i -> threadPool.execute(generateRunnable(i, 1000, 1000)));


        long startAt = System.nanoTime();
        threadPool.start();
        threadPool.requestShutdown();
        threadPool.join();

        double elapsedInSeconds = (System.nanoTime() - startAt) / 1_000_000_000.0;
        System.out.println(threadPool);
        System.out.println(STR."Elapsed time: \{elapsedInSeconds} seconds");


    }


    static class ThreadPool extends Thread {

        private final int maxRunningThreads;
        private final int poolSize;
        private final List<PoolThread> pool;
        private final Queue<Runnable> runnableQueue;

        private int runningThreadsCount;
        private boolean active;
        private boolean shutdownRequested;

        /**
         * Pool Size is 1 to 50 (Checked)
         * Max Running Threads count is 1 to PoolSize (Checked)
         */
        public ThreadPool(int maxRunningThreads, int poolSize, String poolName) {
            super(poolName);
            this.poolSize = Math.max(1, Math.min(50, poolSize));
            this.runningThreadsCount = 0;
            this.maxRunningThreads = Math.max(1, Math.min(this.poolSize, maxRunningThreads));
            this.pool = new ArrayList<>(this.poolSize);

            this.active = true;
            this.shutdownRequested = false;

            this.runnableQueue = new LinkedList<>();

            // TODO: Lazy Creation of threads
            IntStream.range(0, poolSize)
                    .forEach(i -> this.pool.add(new PoolThread(STR."PoolThread-\{i}", this)));
        }

        @Override
        public void run() {
            while (active) {
                if (!runnableQueue.isEmpty() && runningThreadsCount < maxRunningThreads) {

                    this.pool.stream()
                            .filter(PoolThread::isAvailable)
                            .findFirst()
                            .ifPresent(poolThread -> {
                                poolThread.execute(runnableQueue.remove());
                                if (!poolThread.isAlive()) {
                                    poolThread.start();
                                }
                                this.incrementRunningThreadCount();
                            });

                } else if (runnableQueue.isEmpty() && this.shutdownRequested) {
                    active = false;
                }
                ThreadPool.makeThreadHealthy();
            }
            this.pool.forEach(PoolThread::requestShutdown);
            System.out.println(STR."[ThreadPool]-[\{this.getName()}] --- Stopped");
        }

        @Override
        public String toString() {
            return STR."ThreadPool { poolSize: \{poolSize}, maxRunningThread: \{maxRunningThreads}, isActive: \{active} }";
        }

        synchronized void incrementRunningThreadCount() {
            System.out.println("New task requested");
            this.runningThreadsCount++;
        }

        synchronized void decrementRunningThreadCount() {
            this.runningThreadsCount--;
        }

        void execute(Runnable runnable) {
            if (shutdownRequested) {
                return;
            }
            this.runnableQueue.offer(runnable);
        }

        void requestShutdown() {
            this.shutdownRequested = true;
        }

        static void makeThreadHealthy() {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        private static final class PoolThread extends Thread {

            private Runnable runnable;
            private boolean active;
            private boolean shutdownRequested;
            private final ThreadPool pool;

            PoolThread(String name, ThreadPool pool) {
                super(name);
                this.pool = pool;
                active = true;
                shutdownRequested = false;
            }

            void execute(Runnable runnable) {
                if (shutdownRequested) {
                    return;
                }
                this.runnable = runnable;
            }

            boolean isAvailable() {
                return this.runnable == null;
            }

            void requestShutdown() {
                this.shutdownRequested = true;
            }

            @Override
            public void run() {
                while (active) {
                    if (runnable != null) {
                        runnable.run();
                        runnable = null;
                        this.pool.decrementRunningThreadCount();
                    }
                    if (shutdownRequested) {
                        this.active = false;
                    }
                    ThreadPool.makeThreadHealthy();
                }
            }


        }

    }
}
