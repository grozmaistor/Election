package grozdan.test.election.web.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BallotRequest(@JsonProperty("ballot") int ballot) {
}
