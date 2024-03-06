package grozdan.test.election.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FixedSizeQueueTest {
    @Test
    public void testQueueOperations() {
        VoteValidator.FixedSizeQueue<LocalDateTime> queue = new VoteValidator.FixedSizeQueue<>(3);

        // get from an empty queue
        assertEquals(3, queue.getMaxSize());
        assertEquals(0, queue.getSize());
        assertNull(queue.peek());
        assertNull(queue.poll());
        assertEquals(0, queue.getSize());

        // start adding elements and then retrieving them back
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);
        LocalDateTime nowPlus2Days = now.plusDays(2);
        LocalDateTime nowPlus3Days = now.plusDays(3);

        queue.add(now);
        assertEquals(1, queue.getSize());
        assertEquals(now, queue.peek());

        queue.add(tomorrow);
        assertEquals(2, queue.getSize());
        queue.add(nowPlus2Days);
        assertEquals(3, queue.getSize());

        queue.add(nowPlus3Days);
        assertEquals(3, queue.getSize());
        assertEquals(tomorrow, queue.peek());

        LocalDateTime head = queue.poll();
        assertEquals(tomorrow, head);
        assertEquals(2, queue.getSize());
        assertEquals(nowPlus2Days, queue.peek());

        head = queue.poll();
        assertEquals(nowPlus2Days, head);
        assertEquals(1, queue.getSize());
        assertEquals(nowPlus3Days, queue.peek());

        // check queue is empty again
        head = queue.poll();
        assertEquals(nowPlus3Days, head);
        assertEquals(0, queue.getSize());
        assertNull(queue.peek());

        assertNull(queue.poll());
        assertEquals(0, queue.getSize());
        assertEquals(3, queue.getMaxSize());
    }
}
