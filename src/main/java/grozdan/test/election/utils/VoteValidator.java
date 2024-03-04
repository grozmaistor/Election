package grozdan.test.election.utils;

import java.time.LocalDateTime;
import java.util.*;

public enum VoteValidator {
    INSTANCE;

    public final static int WINNER_CHECK_LIMIT = 3;
    public final static int WINNER_CHECK_PER_MINUTES = 1;
    private final Set<String> votedAddressesCache = Collections.synchronizedSet(new HashSet<>());
    private final Map<String, FixedSizeQueue<LocalDateTime>> checkWinnerCache = new HashMap<>();

    public boolean hasVoted(String ipAddress) {
        return votedAddressesCache.contains(ipAddress);
    }

    public void markVoted(String ipAddress) {
        votedAddressesCache.add(ipAddress);
    }

    public boolean winnerCheckAllowed(String ipAddress) {
        LocalDateTime now = LocalDateTime.now();
        if (checkWinnerCache.containsKey(ipAddress)) {
            FixedSizeQueue<LocalDateTime> queue = checkWinnerCache.get(ipAddress);
            synchronized (queue) {
                if (queue.getSize() < WINNER_CHECK_LIMIT) {
                    queue.add(now);
                    return true;
                }
                LocalDateTime threshold = now.minusMinutes(WINNER_CHECK_PER_MINUTES);
                if (threshold.isAfter(queue.peek())) {
                    queue.add(now);
                    return true;
                }
                return false;
            }
        } else {
            FixedSizeQueue<LocalDateTime> queue = new FixedSizeQueue<>(WINNER_CHECK_LIMIT);
            queue.add(now);
            checkWinnerCache.put(ipAddress, queue);
            return true;
        }
    }

    public void reset() {
        votedAddressesCache.clear();
        checkWinnerCache.clear();
    }
    /**
     * Fixed size queue with automatic head element removal.
     * When the queue is full, adding an element will result in automatic removal of the head element.
     * This class is thread safe.
     * @param <E>
     */
    public static class FixedSizeQueue<E> {
        private final int size;
        private volatile int elementCount = 0;
        private final Queue<E> queue = new LinkedList<>();

        public FixedSizeQueue(int size) {
            this.size = size;
        }
        public synchronized void add(E e) {
            if(elementCount == size) {
                queue.poll();
            } else {
                elementCount++;
            }
            queue.add(e);
        }
        public synchronized E poll() {
            if (elementCount > 0) {
                elementCount--;
                return queue.poll();
            } else {
                return null;
            }
        }
        public synchronized E peek() {
            return queue.peek();
        }
        public synchronized int getSize() {
            return size;
        }
    }
}
