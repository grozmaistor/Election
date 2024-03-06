package grozdan.test.election.core;

import java.time.LocalDateTime;
import java.util.*;

public enum ElectionManager {
    INSTANCE;
    private volatile VotingEngine votingEngine = null;

    /**
     * Create new election and return its election id
     * @return election id
     */
    public synchronized UUID createElection(int ballotCount, long registeredVoters, LocalDateTime startDateTime, LocalDateTime endDateTime) throws ElectionException {
        if (hasWaitingElection() || hasRunningElection()) {
            throw new ElectionException("There is already a waiting/running election there!");
        }

        if (votingEngine != null) {
            //store last results in archive
            putInArchive(votingEngine);
        }

        UUID id = getNextElectionUUID();
        votingEngine = new VotingEngine(id, ballotCount, registeredVoters, startDateTime, endDateTime);

        return id;
    }

    public boolean hasElection() {
        return votingEngine != null;
    }
    public boolean hasWaitingElection() {
        return votingEngine != null && votingEngine.isWaiting();
    }
    public boolean hasRunningElection() {
        return votingEngine != null && !votingEngine.isWaiting() && !votingEngine.isClosed();
    }
    public boolean hasWinner() {
        return votingEngine!=null && votingEngine.hasWinner();
    }
    public int getWinner() throws ElectionException {
        if (votingEngine!=null) {
            return votingEngine.getWinner();
        }
        throw new ElectionException("No election is created");
    }

    private static UUID getNextElectionUUID() {
        return UUID.randomUUID();
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
        System.out.println("Election with id: " + votingEngine.getId().toString() + " was put in the archive.");
    }

    public void countVote(String ipAddress, int ballot) throws ElectionException {
        votingEngine.countVote(ipAddress, ballot);
    }
}
