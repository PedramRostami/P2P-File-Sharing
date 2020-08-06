package fileSharing;

import java.net.DatagramSocket;

public class Timeout extends Thread{
    private DatagramSocket ds;
    private volatile boolean running = true;
    public Timeout(DatagramSocket ds) {
        this.ds = ds;
    }

    public Timeout() {

    }

    @Override
    public void run() {
        super.run();
        for (int i = 0; i < 6 && running; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ds.close();
    }

    public void terminate() {
        running = false;
    }

    public DatagramSocket getDs() {
        return ds;
    }

    public void setDs(DatagramSocket ds) {
        this.ds = ds;
    }
}
