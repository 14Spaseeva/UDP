package Client;

import CommonUtils.Stopable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ASPA on 26.05.2017.
 */
public class Timer implements Stopable{
    private static Logger log = LoggerFactory.getLogger("Timer");

    boolean status;
    private SlidingWindowController slidingWindowController;

    public Timer(SlidingWindowController slidingWindowController){
        this.slidingWindowController = slidingWindowController;
    }

    @Override
    public void stop() {
        status=false;
    }

    @Override
    public void run() {
        while(status){
            slidingWindowController.doTimerIteration();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                log.error("cant sleep", e);
            }
        }
    }
}
