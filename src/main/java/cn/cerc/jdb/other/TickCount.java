package cn.cerc.jdb.other;

import org.apache.log4j.Logger;

public class TickCount {
    private static final Logger log = Logger.getLogger(TickCount.class);
    private long lastTime;

    public TickCount() {
        this.lastTime = System.currentTimeMillis();
    }

    public void print(String message) {
        log.info(String.format("%s tickCount: %s", message, System.currentTimeMillis() - lastTime));
        this.lastTime = System.currentTimeMillis();
    }

    public static void main(String[] args) {
        TickCount tick = new TickCount();
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(100);
                tick.print("test");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
