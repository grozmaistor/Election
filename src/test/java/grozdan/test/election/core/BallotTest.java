package grozdan.test.election.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static grozdan.test.election.core.VotingEngine.Ballot;

public class BallotTest {

    @Test
    public void testBallotVoteComparator() {
        Ballot b1 = new Ballot(1);
        Ballot b2 = new Ballot(2);

        Comparator<Ballot> c = Ballot.voteComparator();

        assertEquals(0, c.compare(b1, new Ballot(1)));

        assertEquals(-1, c.compare(b1, b2));
        assertEquals(1, c.compare(b2, b1));

        b1.countVote();
        assertEquals(-1, c.compare(b1, b2));

        b2.countVote();
        b2.countVote();
        assertEquals(1, c.compare(b1, b2));
    }

    @Test
    public void testBallotOrderingInTreeSet() {
        Ballot b1 = new Ballot(1);
        Ballot b2 = new Ballot(2);
        Ballot b3 = new Ballot(3);
        Ballot b4 = new Ballot(4);


        countVotes(b1, 7);
        countVotes(b2, 5);
        countVotes(b3, 3);
        countVotes(b4, 1);

        Ballot [] expectedBallots = {b1, b2, b3, b4};

        Set<Ballot> ballotSet = new TreeSet<>(Ballot.voteComparator());
        ballotSet.add(b1);
        ballotSet.add(b2);
        ballotSet.add(b3);
        ballotSet.add(b4);

        int [] arrI = {0};
        ballotSet.forEach( ballot -> {
            System.out.println(expectedBallots[arrI[0]]);
            assertEquals(expectedBallots[arrI[0]++], ballot);
        });

        ballotSet.remove(b2);
        countVotes(b2, 4);
        ballotSet.add(b2);
        Optional<Ballot> first = ballotSet.stream().findFirst();
        Ballot result = first.get();
        assertEquals(b2, result);
        System.out.println(result);
    }

    private void countVotes(Ballot b, int votesNumber) {
        for (int i = 0; i<votesNumber; i++) {
            b.countVote();
        }
    }
}
