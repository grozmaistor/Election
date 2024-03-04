package grozdan.test.election.core;

import java.time.LocalDateTime;
import java.util.UUID;
import static grozdan.test.election.core.VotingEngine.Ballot;

public record ElectionResult(UUID id, LocalDateTime startDateTime, LocalDateTime endDateTime,
                             long registeredVoters, long voted, int ballotCount, Ballot[] ballots) {
}
