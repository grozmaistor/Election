package grozdan.test.election.web.impl;

import grozdan.test.election.core.ElectionException;
import grozdan.test.election.core.ElectionManager;
import grozdan.test.election.utils.DateTimeUtils;
import grozdan.test.election.utils.VoteValidator;
import grozdan.test.election.web.api.BallotRequest;
import grozdan.test.election.web.api.ElectionController;
import grozdan.test.election.web.api.ElectionRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/election")
public class ElectionControllerImpl implements ElectionController {

    @Autowired
    ElectionService service;

    @Override
    public ResponseEntity<String> createElection(@RequestBody ElectionRequest electionRequest) {
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        try {
            startDateTime = DateTimeUtils.parse(electionRequest.startDateTime());
            endDateTime = DateTimeUtils.parse(electionRequest.endDateTime());
        } catch (ElectionException dte) {
            return ResponseEntity.badRequest().body("Start & End election dates must be in the required format: 'yyyy-MM-dd hh:mm:ss'.");
        }

        try {
            VoteValidator.INSTANCE.validateCreateParameters(
                    electionRequest.ballotCount(), electionRequest.registeredVoters(), startDateTime, endDateTime);
        } catch (ElectionException e) {
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }

        try {
            ElectionManager.INSTANCE.createElection(
                    electionRequest.ballotCount(),
                    electionRequest.registeredVoters(),
                    startDateTime,
                    endDateTime);
            return ResponseEntity.status (HttpStatus.CREATED).body("Created new election");
        } catch (ElectionException e) {
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> vote(@RequestBody BallotRequest ballotRequest, HttpServletRequest request) {
        if (!ElectionManager.INSTANCE.hasElection()) {
            return ResponseEntity.status(HttpStatus.OK).body("No election has been created.");
        }
        if (ElectionManager.INSTANCE.hasWaitingElection()) {
            return ResponseEntity.status(HttpStatus.OK).body("The election has not started yet.");
        }

        if (ElectionManager.INSTANCE.hasRunningElection()) {
            try {
                ElectionManager.INSTANCE.countVote(request.getRemoteAddr(), ballotRequest.ballot());
                return ResponseEntity.status(HttpStatus.OK).body("Vote counted");
            } catch (ElectionException e) {
                return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("The election has finished.");
        }
    }

    @Override
    public ResponseEntity<String> checkWinner(HttpServletRequest request) {
        String remoteAddress = request.getRemoteAddr();

        if (VoteValidator.INSTANCE.winnerCheckAllowed(remoteAddress)) {
            if (ElectionManager.INSTANCE.hasElection()) {

                if (ElectionManager.INSTANCE.hasWaitingElection()) {
                    return ResponseEntity.status(HttpStatus.OK).body("No one can check if a winner is declared before the start date of an election");
                }

                if (ElectionManager.INSTANCE.hasWinner()) {
                    try {
                        return ResponseEntity.status(HttpStatus.OK).body("" + ElectionManager.INSTANCE.getWinner());
                    } catch (ElectionException e) {
                        return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.OK).body("No winner found yet.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.OK).body("No election is created.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("A single IP address can't check if there's a winner more than 3 times every minute.");
        }
    }

    @Override
    public ResponseEntity<String> getElectionFromArchive() {
        return ResponseEntity.status(HttpStatus.OK).body("Not implemented.");
    }
}
