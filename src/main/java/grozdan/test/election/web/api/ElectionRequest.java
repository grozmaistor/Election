package grozdan.test.election.web.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record ElectionRequest(
    @JsonProperty("candidates")
    int ballotCount,

    @JsonProperty("registeredVoters")
    long registeredVoters,

    @JsonProperty("startDateTime")
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    String startDateTime,

    @JsonProperty("endDateTime")
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    String endDateTime
) {
    public final static String DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
}
