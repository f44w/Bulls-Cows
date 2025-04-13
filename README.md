# CS207 - Advanced Programming
## Bulls and Cows Project
### Authors: Fraser Watt, Alexe Boyd, Ross Paton, Jacob Mwangu, Finlay Colston

![image info](Sample_Solution_Class_Diagram_Bulls_and_Cows.png)
### Summary:
Bulls and Cows is a code breaking game.
Aim of the game is to decipher secret codes by trial and error.
Code can be either a word or number.
Words are English words, and the numbers are all unique (1232 is invalid).
For each guess, the number of matches will be given, split into bulls and cows.
A cow matches in the wrong position, a bull matches in the correct position.

### Example:
- Code: 1359
- User guess: 1395

- Matches: 2 bulls and 2 cows (Bulls: 1, 3 Cows: 9, 5)

### Notes:
- Saved codes should have the number of guesses, cows, and bulls too
- Only one code saved at a time per player, if there is already a saved code give the user the option of overriding it.
- Every time a user enters a guess, the number of bulls and cows needs to be sent to the player's stats. 
- Stats are shown by what percentage of your total  guesses are bulls or cows 
- Every time a new code is created, the number of codes attempted need to be incremented 
- When loading user details, if the details provided by the user are not already stored, prompt the user to create a new profile.
- Saving your details should also send the player name and total number of codes deciphered to the leaderboard to see if you qualify for the top10
- User must enter unique digits (cannot input 1111)
- Users can submit the same code more than once, even if its wrong 
- Scoreboard will be based on the number of correctly deciphered codes
- Users will have to register an account before playing/to get on the scoreboard (possibly with just a name idk about a password)
- Users should be able to see statistics on themselves 
- Options to give up, get a hint, or start a new round 
- No guess limit

### Questions:


### To-Do
- Start writing final document for submission
- Start writing JUnits for Sprint 3 
- Test everything