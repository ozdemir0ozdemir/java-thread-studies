package ozdemir0ozdemir.javaconcurrency;

public class FrameExchanger01 {


    void main() throws InterruptedException {

        Exchanger exchanger = new Exchanger();
        Thread t1 = Thread.ofVirtual().start(() -> {
            for(int i = 1; i <= 100_000; i++){
                exchanger.storeFrame(new Frame("Frame-" + i));
            }
        });

        Thread t2 = Thread.ofVirtual().start(() -> {
            for(int i = 1; i <= 100_000; i++){
                exchanger.takeFrame();
            }
        });


        t1.join();
        t2.join();

        System.out.println(STR."------Frames Stored : \{exchanger.framesStoredCount}");
        System.out.println(STR."------Frames Taken : \{exchanger.framesTakenCount}");
        System.out.println(STR."Last Frame Taken : \{exchanger.frame}");
    }

    static class Exchanger {

        private long framesStoredCount = 0;
        private long framesTakenCount = 0;

        private volatile boolean newFrameExists = false;
        private Frame frame = null;

        void storeFrame(Frame frame) {

            this.frame = frame;
            this.framesStoredCount++;
            this.newFrameExists = true;
        }

        Frame takeFrame() {

            Frame f = this.frame;
            this.framesTakenCount++;
            this.newFrameExists = false;
            return f;
        }
    }

    record Frame(String name) {
    }
}
