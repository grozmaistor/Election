package grozdan.test.election.web.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
public interface ElectionController {
    @PostMapping(value = "/election", produces = "application/json")
    @ResponseBody
    ResponseEntity<String> createElection(@RequestBody ElectionRequest electionRequest);

    @PostMapping(value = "/election/ballot", produces = "application/json")
    @ResponseBody
    ResponseEntity<String> vote(@RequestBody BallotRequest ballotRequest, HttpServletRequest request);

    @GetMapping(value = "/election", produces = "application/json")
    @ResponseBody
    ResponseEntity<String> checkWinner(HttpServletRequest request);

    @GetMapping("/elections")
    @ResponseBody
    ResponseEntity<String> getAllElectionsFromArchive();

    @GetMapping("/elections/{id}")
    @ResponseBody
    ResponseEntity<String> getElectionFromArchive();
}
