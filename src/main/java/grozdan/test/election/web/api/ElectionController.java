package grozdan.test.election.web.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/election")
public interface ElectionController {
    @PostMapping
    @ResponseBody
    ResponseEntity<String> createElection(@RequestBody ElectionRequest electionRequest);

    @PutMapping
    @ResponseBody
    ResponseEntity<String> vote(@RequestBody BallotRequest ballotRequest, HttpServletRequest request);

    @GetMapping
    @ResponseBody
    ResponseEntity<String> checkWinner(HttpServletRequest request);

    @GetMapping("/{id}")
    @ResponseBody
    ResponseEntity<String> getElectionFromArchive();
}
