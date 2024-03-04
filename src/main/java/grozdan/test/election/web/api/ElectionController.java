package grozdan.test.election.web.api;

import grozdan.test.election.core.ElectionManager;
import grozdan.test.election.utils.VoteValidator;
import grozdan.test.election.web.impl.ElectionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ElectionController {

    @Autowired
    ElectionService service;

    @PostMapping("/election")
    public String createElection() {
        return "{ createElection: \"\" }";
    }

    @PutMapping("/vote")
    public String vote() {
        return "{ vote: \"\" }";
    }

    @GetMapping("/election")
    public String checkWinner(HttpServletRequest request) {
        String remoteAddress = request.getRemoteAddr();
        if (VoteValidator.INSTANCE.winnerCheckAllowed(remoteAddress)) {
            if (ElectionManager.INSTANCE.hasElection()) {
                return "{ winner: \"No winner found yet.\" }";
            } else {
                return "{ error: \"No election is created.\"}";
            }
        } else {
            return "{ error: \"A single IP address can't check if there's a winner more than 3 times every minute.\"}";
        }
    }

    @GetMapping("/archive/{id}")
    public String getElectionFromArchive() {
        return "{ status: \"No winner found yet.\" }";
    }
}
