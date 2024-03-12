# E L E C T I O N
powered by SpringBoot
<br><br><br>

### API calls:

1. To create a new Election send a POST request with JSON body containing parameters for *candidates*, *registeredVoters*, *startDateTime* and *endDateTime*.
- POST: ~/election
- JSON body: {
  "candidates": 3,
  "registeredVoters": 11,
  "startDateTime": "2024-03-06 07:00:00",
  "endDateTime": "2024-03-06 20:00:00"
  }
#
2. To vote send a PUT request with JSON body containing the *ballot* number.
- PUT: ~/election
- JSON body:
  {
  "ballot": 3
  } 
#
3. To check if a winner exists send a GET request with no parameters.
- GET: ~/election

<br>
<br>

### HINTS

When creating a new election, if start and end election dates are a valid range but both are in the past, then an election will be created and closed with zero voters and the lowest number ballot as a winner.
<br><br>
This can be also seen in the console:
<br>
<p>
Election is open / Start of voting.<br>
Election is closed / End of voting.<br>
Ballot 1 is the winner!
</p>
<br>
While voting a winner may be elected. The winning ballot will be logged in the console and can be checked by an api call. But the election will not be closed unless the end date.
After declaring a winner, but before the end date, other voters who haven't voted yet may still vote.

<br>
<br>

### POSTMAN
The *postman* folder contains predefined collection of API calls that can be used to call the server.

<br>
<br>

### TODO:
1. Improve endpoint names and method calls.
2. Improve error handling and messaging
3. Add SpringBoot Service layer between the rest controller and the ElectionManager.