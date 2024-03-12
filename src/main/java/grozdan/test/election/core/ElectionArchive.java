package grozdan.test.election.core;

import java.util.HashMap;
import java.util.Map;

/**
 * A singleton instance to store all completed elections.
 */
public enum ElectionArchive {
    INSTANCE;
    private final Map<String, ElectionData> archive = new HashMap<>();
    public static ElectionData get(String id) throws ElectionException {
        if (INSTANCE.archive.containsKey(id)) {
            return INSTANCE.archive.get(id);
        } else {
            throw new ElectionException("No such Election!");
        }
    }

    public static void put(ElectionData e) throws ElectionException {
        if (INSTANCE.archive.containsKey(e.id().toString())) {
            throw new ElectionException("Election already exists in archive!");
        } else {
            INSTANCE.archive.put(e.id().toString(), e);
        }
    }

    public static long size() {
        return INSTANCE.archive.size();
    }
}
