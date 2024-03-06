package grozdan.test.election.core;

import java.time.LocalDateTime;
import java.util.*;

public class VotingEngine implements Runnable {
    public static class Ballot {
        private final int number;
        private long votes = 0;
        public Ballot(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }

        public long getVotes() {
            return votes;
        }

        public void countVote() {
            votes++;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (o instanceof Ballot) {
                return this.number == ((Ballot) o).number;
            }
            return false;
        }
        @Override
        public int hashCode() {
            return this.number;
        }

        /**
         * This comparator will sort a collection of Ballots by their vote in descending order.
         * If two ballots have same vote value, the ballot with lower number will be before the other one.
         * @return Comparator of Ballot elements.
         */
        public static Comparator<Ballot> voteComparator() {
            return (o1, o2) -> o1.votes > o2.votes ? -1 : o1.votes < o2.votes ? 1 : (o1.number - o2.number);
        }

        @Override
        public String toString() {
            return new StringBuilder("Ballot[number: ").append(number).append(", votes: ").append(votes).append("]").toString();
        }
    }
    private UUID id;
    private int ballotCount;
    private long registeredVoters = 1_000_000_000; //default value
    private volatile int voted = 0;
    private LocalDateTime startDateTime = null;
    private LocalDateTime endDateTime = null;

    private Ballot [] ballots;
    private volatile Set<Ballot> ballotSet = new TreeSet<>(Ballot.voteComparator());
    private volatile Ballot winningBallot = null;

    public VotingEngine(UUID id, int ballotCount, long registeredVoters, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        init(id, ballotCount, registeredVoters, startDateTime, endDateTime);
    }

    private void init(UUID id, int ballotCount, long registeredVoters, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.id = id;
        this.ballotCount = ballotCount;
        this.registeredVoters = registeredVoters;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;

        this.voted = 0;
        this.winningBallot = null;
        this.ballots = new Ballot[ballotCount];
        this.ballotSet.clear();
        for (int i = 0; i < this.ballotCount; i++) {
            Ballot ballot = new Ballot(i+1);
            ballots[i] = ballot;
            ballotSet.add(ballot);
        }
    }

    public void countVote(int ballotNumber) throws ElectionException {
        if (isRunning()) {
            if (ballotNumber < 1 || ballotNumber > ballotCount) {
                throw new ElectionException("A candidate with such number doesn't exist.");
            }
            if (voted >= registeredVoters) {
                throw new ElectionException("All voters have voted.");
            }

            synchronized (this) {
                if (isRunning()) {
                    Ballot b = ballots[ballotNumber - 1];
                    ballotSet.remove(b);
                    b.votes++;
                    ballotSet.add(b);
                    voted++;

                    checkWinner();
                }
            }
        }
    }

    private void checkWinner() throws ElectionException {
        if (winningBallot == null) {
            Ballot maxVotedBallot = getFirstBallot();

            // A ballot has > 50% of the votes.
            if (maxVotedBallot.votes > registeredVoters / 2) {
                setWinningBallot(maxVotedBallot);
                return;
            }

            // maxVotedBallot - runningAfterBallot > remainingVotes
            Optional<Ballot> runningAfterBallotOptional = ballotSet.stream().skip(1).findFirst();
            if (runningAfterBallotOptional.isEmpty()) {
                setWinningBallot(maxVotedBallot);      // Only one ballot in the election.
            } else {
                Ballot runningAfterBallot = runningAfterBallotOptional.get();
                if (maxVotedBallot.votes - runningAfterBallot.votes > registeredVoters - voted) {
                    setWinningBallot(maxVotedBallot);  // maxVotedBallot - runningAfterBallot > remainingVotes
                }
            }
        }
    }

    private synchronized void setWinningBallot(final Ballot maxVotedBallot) {
        System.out.println("Ballot " + maxVotedBallot.getNumber() + " is the winner!");
        winningBallot = maxVotedBallot;
    }

    private Ballot getFirstBallot() throws ElectionException {
        Optional<Ballot> ballotOptional = ballotSet.stream().findFirst();
        if (ballotOptional.isEmpty()) {
            // This should never happen if initialization is correct!
            throw new ElectionException("There is no ballot in the election!");
        }
        return ballotOptional.get();
    }

    public boolean hasWinner() {
        return winningBallot != null;
    }

    public int getWinner() throws ElectionException {
        if (winningBallot != null) {
            return winningBallot.number;
        } else {
            throw new ElectionException("There is no winner elected yet!");
        }
    }

    public boolean isWaiting() {
        return LocalDateTime.now().isBefore(startDateTime);
    }

    public boolean isClosed() {
        return LocalDateTime.now().isAfter(endDateTime);
    }

    public boolean isRunning() {
        return ! isClosed() && ! isWaiting();
    }

    @Override
    public void run() {
        while(isWaiting()) {
            try {
                //TODO: replace with Timer
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Do nothing
            }
        }
        System.out.println("Election is open / Start of voting.");
        while(!isClosed()) {
            try {
                //TODO: replace with Timer
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Do nothing
            }
        }
        System.out.println("Election is closed / End of voting.");
        try {
            setWinningBallot(getFirstBallot());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UUID getId() {
        return id;
    }
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public int getBallotCount() {
        return ballotCount;
    }

    public long getRegisteredVoters() {
        return registeredVoters;
    }

    public int getVoted() {
        return voted;
    }

    public Ballot[] getBallots() {
        return ballots;
    }
}
