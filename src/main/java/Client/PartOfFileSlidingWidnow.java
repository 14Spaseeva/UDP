package Client;


import CommonUtils.ConcurrentSlidingWindow;
import CommonUtils.TimedPartOfFile;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;


  class PartOfFileSlidingWidnow extends ConcurrentSlidingWindow<TimedPartOfFile> {

    PartOfFileSlidingWidnow(int size) {
        super(size);
    }

    /**
     * return stream of package without confirmation
     *
     * @param timeOutInMilliseconds timeout after it package should have confirm
     * @return Stream of timeout packages
     */
    Stream<TimedPartOfFile> getNotConfirmedParts(long timeOutInMilliseconds) {
        synchronized (lock) {
            long now = System.currentTimeMillis();
            return IntStream.range(getCurrentStart(), getCurrentEnd())
                    .mapToObj(this::get)
                    .filter(timedPartOfFile -> timedPartOfFile != null
                            && !timedPartOfFile.getConfirm()
                            && timedPartOfFile.gettimeOfSending() != 0
                            && now - timedPartOfFile.gettimeOfSending() > timeOutInMilliseconds);
        }
    }

    /**
     * move window while number of packages in the front of window follow each other
     *
     * @return List of free packages after moving. Reuse data from them
     */
    public List<TimedPartOfFile> moveWindow() {
        synchronized (lock) {
            LinkedList<TimedPartOfFile> result = new LinkedList<>();
            while (get(getCurrentStart()).getConfirm()) {
                result.add(move());
            }
            return result;
        }
    }

    /**
     * set sending time
     *
     * @param number  package number
     * @param sending time of sending
     */
    public void setSendingTime(int number, long sending) {
        synchronized (lock) {
            if (number >= getCurrentStart()) {
                try {
                    get(number).settimeOfSending(sending);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Set some package confirm
     *
     * @param number number of package
     */
    public void setConfirm(int number) {
        synchronized (lock) {
            if (number >= getCurrentStart())
                get(number).setConfirm();
        }
    }

}
