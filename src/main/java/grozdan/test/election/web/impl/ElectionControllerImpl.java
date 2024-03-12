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
@RequestMapping("/api/v1")
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
            return ClientResponse.toBadRequest("Start & End election dates must be in the required format: 'yyyy-MM-dd hh:mm:ss'.");
        }

        try {
            VoteValidator.INSTANCE.validateCreateParameters(
                    electionRequest.ballotCount(), electionRequest.registeredVoters(), startDateTime, endDateTime);
        } catch (ElectionException e) {
            return ClientResponse.toBadRequest(e.getMessage());
        }

        try {
            ElectionManager.INSTANCE.createElection(
                    electionRequest.ballotCount(),
                    electionRequest.registeredVoters(),
                    startDateTime,
                    endDateTime);
            System.out.println("Created new election");
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (ElectionException e) {
            return ClientResponse.toBadRequest(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> vote(@RequestBody BallotRequest ballotRequest, HttpServletRequest request) {
        if (!ElectionManager.INSTANCE.hasElection()) {
            return ClientResponse.toBadRequest("No election has been created.");
        }
        if (ElectionManager.INSTANCE.hasWaitingElection()) {
            return ClientResponse.toBadRequest("The election has not started yet.");
        }

        if (ElectionManager.INSTANCE.hasRunningElection()) {
            try {
                ElectionManager.INSTANCE.countVote(request.getRemoteAddr(), ballotRequest.ballot());
                return ClientResponse.toOK("Vote counted");
            } catch (ElectionException e) {
                return ClientResponse.toBadRequest(e.getMessage());
            }
        } else {
            return ClientResponse.toBadRequest("The election has finished.");
        }
    }

    @Override
    public ResponseEntity<String> checkWinner(HttpServletRequest request) {
        String remoteAddress = request.getRemoteAddr();

        if (VoteValidator.INSTANCE.winnerCheckAllowed(remoteAddress)) {
            if (ElectionManager.INSTANCE.hasElection()) {

                if (ElectionManager.INSTANCE.hasWaitingElection()) {
                    return ClientResponse.toBadRequest("No one can check if a winner is declared before the start date of an election");
                }

                if (ElectionManager.INSTANCE.hasWinner()) {
                    try {
                        return ResponseEntity.status(HttpStatus.OK).body("" + ElectionManager.INSTANCE.getWinner());
                    } catch (ElectionException e) {
                        return ClientResponse.toBadRequest(e.getMessage());
                    }
                } else {
                    return ClientResponse.toOK("No winner found yet.");
                }
            } else {
                return ClientResponse.toBadRequest("No election is created.");
            }
        } else {
            return ClientResponse.toBadRequest("A single IP address can't check if there's a winner more than 3 times every minute.");
        }
    }

    @Override
    public ResponseEntity<String> getAllElectionsFromArchive() {
        return ClientResponse.toOK("Not implemented.");
    }

    @Override
    public ResponseEntity<String> getElectionFromArchive() {
        return ClientResponse.toOK("Not implemented.");
    }
}