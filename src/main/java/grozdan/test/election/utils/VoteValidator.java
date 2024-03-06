package grozdan.test.election.utils;

import grozdan.test.election.core.ElectionException;

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
        private final int maxSize;
        private volatile int size = 0;
        private final Queue<E> queue = new LinkedList<>();

        public FixedSizeQueue(int maxSize) {
            this.maxSize = maxSize;
        }
        public synchronized void add(E e) {
            if(size == maxSize) {
                queue.poll();
            } else {
                size++;
            }
            queue.add(e);
        }
        public synchronized E poll() {
            if (size > 0) {
                size--;
                return queue.poll();
            } else {
                return null;
            }
        }
        public synchronized E peek() {
            return queue.peek();
        }
        public int getMaxSize() {
            return maxSize;
        }
        public synchronized int getSize() {
            return size;
        }
    }

    public void authenticate(String ipAddress) throws ElectionException {
        if (!isLocalHost(ipAddress)) {
            System.out.println("IP address " + ipAddress + " was not authenticated!");
            throw new ElectionException("Request is allowed only if it's made from IP address = 127.0.0.1");
        }
        System.out.println("IP address " + ipAddress + " was successfully authenticated.");
    }

    private boolean isLocalHost(String ipAddress) {
        return true;
    }
}
