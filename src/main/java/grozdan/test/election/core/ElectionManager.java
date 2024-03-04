package grozdan.test.election.core;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum ElectionManager {
    INSTANCE;
    public static final int MAX_ENGINES = 1;
    private ExecutorService executor = Executors.newFixedThreadPool(MAX_ENGINES);
    private volatile UUID currentElectionId;
    //private VotingEngine[] votingEngines = new VotingEngine[MAX_ENGINES];

    private volatile VotingEngine votingEngine = null;
    private Set<String> ipAddressCache = new HashSet<>();

    /**
     * Create new election and return its election id
     * @return election id
     */
    public synchronized UUID createElection(int ballotCount, long registeredVoters, LocalDateTime startDateTime, LocalDateTime endDateTime) throws ElectionException {
        if (hasRunningElection()) {
            throw new ElectionException("There is already a running election there!");
        }

        if (votingEngine != null) {
            //store last results in archive
            putInArchive(votingEngine);
        }

        UUID id = getNextElectionUUID();
        votingEngine = new VotingEngine(id, ballotCount, registeredVoters, startDateTime, endDateTime);
        try {
            executor.submit(votingEngine);
        } catch (Exception e) {
            throw new ElectionException("VotingEngine error!", e);
        }

        return id;
    }

    public boolean hasRunningElection() {
        return votingEngine != null && ! votingEngine.isClosed();
    }

    public boolean hasElection() {
        return votingEngine != null;
    }

    private static UUID getNextElectionUUID() {
        return UUID.randomUUID();
    }

    public void close() {
        if (executor != null && !executor.isShutdown()) {
            try {
                executor.shutdownNow();
                executor = null;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private boolean hasVoted(String ipAddress) {
        return ipAddressCache.contains(ipAddress);
    }

    private void putInArchive(VotingEngine votingEngine) throws ElectionException {
        ElectionResult result = new ElectionResult(
                votingEngine.getId(),
                votingEngine.getStartDateTime(),
                votingEngine.getEndDateTime(),
                votingEngine.getRegisteredVoters(),
                votingEngine.getVoted(),
                votingEngine.getBallotCount(),
                votingEngine.getBallots()
                );
        ElectionArchive.put(result);
    }

}
