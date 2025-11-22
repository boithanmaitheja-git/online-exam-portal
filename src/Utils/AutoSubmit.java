package Utils;

public class AutoSubmit extends Thread {

    private int minutes;
    private boolean timeUp = false;
    private boolean running = true;

    public AutoSubmit(int minutes) {
        this.minutes = minutes;
    }

    public void run() {
        try {
            for (int i = minutes * 60; i >= 0 && running; i--) {
                if (i % 60 == 0) {
                    System.out.println("⏳ Time left: " + (i / 60) + " min");
                }
                Thread.sleep(1000);
            }
            if (running) {
                timeUp = true;
            }
        } catch (InterruptedException e) {
            timeUp = true;
        }
    }

    public boolean isTimeUp() {
        return timeUp;
    }

    public void stopTimer() {
        running = false;
    }
}
