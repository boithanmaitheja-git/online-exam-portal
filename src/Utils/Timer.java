package Utils;

public class Timer extends Thread {

    private int durationSeconds;
    private boolean running = true;
    private boolean timeUp = false;

    public Timer(int minutes) {
        this.durationSeconds = minutes * 60;
    }

    @Override
    public void run() {
        try {
            for (int i = durationSeconds; i >= 0 && running; i--) {
                int minutes = i / 60;
                int seconds = i % 60;
                System.out.printf("\r⏳ Time Left: %02d:%02d", minutes, seconds);
                Thread.sleep(1000);
            }
            if (running) {
                timeUp = true;
                System.out.println("\n⏰ Time is up!");
            }
        } catch (InterruptedException e) {
            System.out.println("\n⚠️ Timer interrupted.");
        }
    }

    public boolean isTimeUp() {
        return timeUp;
    }

    public void stopTimer() {
        running = false;
    }
}
