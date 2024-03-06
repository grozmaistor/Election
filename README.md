# E L E C T I O N
powered by SpringBoot
#

API calls:

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