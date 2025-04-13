import org.junit.jupiter.api.*;
import java.io.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    // captures console output
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    /// ACCEPTANCE CRITERIA 1:
    /// "As a player I want to be able to request a secret code,so I can try to decipher it"

    // Scenario 1: Player requests letters secret code
    @Test
    public void testRequestLettersCode() {

        Player player = new Player("Martin",true);
        Game game = new Game(player);

        game.secretCode = new LettersCode(); // manually create new LetterCode
        player.incrementCodesAttempted(); // simulate internal update


        assertEquals('l', game.getCodeType());
        assertEquals(8, game.getSecretCode().length());
        assertTrue(game.uniqueLetters(game.getSecretCode()));
        assertEquals(1, player.getTotalCodesAttempted());

    }

    // Scenario 2: Player request numbers secret code
    @Test
    public void testRequestNumbersCode() {
        Player player = new Player("Martin", true);
        Game game = new Game(player);

        game.secretCode = new NumbersCode(); // manually create NumberCode
        player.incrementCodesAttempted();    //  // simulate internal update

        String secretCode = game.getSecretCode();

        assertEquals('n', game.getCodeType(), "code type should be numbers");
        assertEquals(8, secretCode.length(), "code should be exactly 4 digits");
        assertTrue(game.uniqueLetters(secretCode), "code should contain unique digits");
        assertEquals(1, player.getTotalCodesAttempted(), "codes attempted should be updated");
    }


    // Scenario 3: Player requests a letters secret code but no phrases file exists
    @Test
    public void testRequestLetterCodeNoFile() {
        LettersCode lettersCode = new LettersCode() {
            protected void generateCode() {
                this.decipheredCode = ""; // code empty to simulate failure
                System.out.println("Error Reading File"); // simulate error message
            }
        };

        Player player = new Player("Martin", true);
        Game game = new Game(player, lettersCode);

        String output = outputStream.toString(); // capture output

        assertTrue(output.contains("Error Reading File"), "expected error -- no file exists");
        assertEquals(0, player.getTotalCodesAttempted(), "codes attempted should not be updated");
    }

    // ACCEPTANCE CRITERIA 2:
    // "As a player I want to be able to enter a guess so I can decipher the secret code "

    // Scenario 1: Player enters a valid guess
    @Test
    public void testValidGuess() {
        Player player = new Player("Martin", true);
        Game game = new Game(player);

        game.secretCode = new NumbersCode(); // manually create new number code
        game.secretCode.setCode("12345678"); // manually set code

        game.enterGuess("12496587"); // simulate user guess -- 2 bulls, 5 cows

        String output = outputStream.toString(); // capture output

        assertTrue(output.contains("Bulls: 2, Cows: 5"), "expected 2 bulls and 5 cows");
        assertEquals(1, player.getTotalNumGuesses());
        assertEquals(2, player.getTotalNumberOfBulls());
        assertEquals(5, player.getTotalNumberOfCows());
    }

    // Scenario 2: Player enters the correct guess and successfully deciphers the code
    @Test
    public void testCorrectGuessDeciphersCode() {
        Player player = new Player("Martin", true);
        Game game = new Game(player);

        game.secretCode = new NumbersCode(); // manually create numbers code
        game.secretCode.setCode("12345678"); // manually set code

        game.enterGuess("12345678"); // simulate correct guess

        String output = outputStream.toString(); // capture output

        assertTrue(output.contains("Congratulations! You deciphered the code!"));
        assertEquals(1, player.getTotalNumGuesses());
        assertEquals(1, player.getTotalCodesDeciphered());
        assertEquals(8, player.getTotalNumberOfBulls());
    }

    // Scenario 3: Player enters guesses of invalid length
    @Test
    public void testInvalidLength() {
        Player player = new Player("Martin", true);
        Game game = new Game(player);

        game.secretCode = new NumbersCode(); // manually create numbers code
        game.secretCode.setCode("12345678"); // manually set code

        game.enterGuess("1234567");     // simuate guess -- too short
        game.enterGuess("123456789");  // simulate guess --too long

        String output = outputStream.toString(); // capture output

        assertTrue(output.contains("Guess Too Short!"), "Expected short guess error");
        assertTrue(output.contains("Guess Too Long!"), "Expected long guess error");
        assertEquals(0, player.getTotalNumGuesses());
    }

    // Scenario 4: Player enters invalid letters guess
    @Test
    public void testInvalidLettersGuess() {
        Player player = new Player("Martin", true);
        Game game = new Game(player);

        game.secretCode = new LettersCode(); // manually create new letter code
        game.secretCode.setCode("freights"); // manually set code

        game.enterGuess("w8576h78");  // simulate invalid guess -- invalid type
        game.enterGuess("fffogggg");  // simulate invalid guess --repeating characters

        String output = outputStream.toString(); // capture output

        assertTrue(output.contains("Code is of type letter"), "Expected letter-type validation error");
        assertTrue(output.contains("Guess must be made up of unique characters"), "Expected uniqueness error");
        assertEquals(0, player.getTotalNumGuesses());
    }

    // Scenario 5: Player enters invalid numbers guess
    @Test
    public void testInvalidNumbersGuess() {
        Player player = new Player("Martin", true);
        Game game = new Game(player);

        game.secretCode = new NumbersCode(); // manually create number code
        game.secretCode.setCode("12345678"); // manually set code

        game.enterGuess("1234567h"); // simulate invalid guess -- contains letter
        game.enterGuess("11111111"); // simulate invalid guess -- duplicate digits

        String output = outputStream.toString(); // capture output

        assertTrue(output.contains("Code is of type number"), "expected number-type validation error");
        assertTrue(output.contains("Guess must be made up of unique characters"), "expected uniqueness error");
        assertEquals(0, player.getTotalNumGuesses());
    }



//  --------------------------------SPRINT TWO STARTS HERE---------------------------------------


/// ACCEPTANCE CRITERIA 3:
/// As a player I want to be able to save a secret code so I can try to decipher it another time:

    // Scenario 1: player saves secret code
    @Test
    void testSaveSecretCode() {
        Player player = new Player("TestSave", true);
        Game game = new Game(player, true);

        game.secretCode = new NumbersCode(); // manually create number code
        game.secretCode.setCode("9876"); // manually set code

        // First save to simulate existing code
        System.setIn(new ByteArrayInputStream("y\n".getBytes())); // simulates "y" to overwrite

        Game.scanner = new Scanner(System.in);
        game.saveCode();

        //check the output
        String output = outputStream.toString();
        System.out.println(output);
        assertTrue(output.contains("Code saved successfully!"));
    }

    /// Scenario 2: player already has a saved secret code
    @Test
    void testSaveSecretCodeWithoutOverwrite() {
        Player player = new Player("NoOverwrite", true);
        Game game = new Game(player, true);

        game.secretCode = new NumbersCode(); // manually create number code
        game.secretCode.setCode("1234"); // manually set code

        // first save
        System.setIn(new ByteArrayInputStream("".getBytes()));
        Game.scanner = new Scanner(System.in);
        game.saveCode();

        // attempt second save and reject overwrite
        game.secretCode.setCode("5678");
        System.setIn(new ByteArrayInputStream("n\n".getBytes()));
        Game.scanner = new Scanner(System.in);
        game.saveCode();

        String output = outputStream.toString(); // capture output

        assertTrue(output.contains("Save operation cancelled"));
    }

    /// ACCEPTANCE CRITERIA 4:
    /// "AS a player I want to be able to load a secret code so I continue trying to decipher a saved secret code":

    // Scenario 1: player loads their saved game
    @Test
    void testLoadSavedCode() {
        Player player = new Player("testLoadPlayer", true);
        Game game = new Game(player, true);

        NumbersCode code = new NumbersCode(); // manually create number code
        code.setCode("12345678"); // manually set code
        game.secretCode = code;

        // Save the code first
        System.setIn(new ByteArrayInputStream("y/n".getBytes()));
        Game.scanner = new Scanner(System.in);
        game.saveCode();

        // Load the saved code
        outputStream.reset();
        System.setIn(new ByteArrayInputStream("".getBytes()));
        Game.scanner = new Scanner(System.in);
        game.loadCode();

        String output = outputStream.toString(); // capture output

        assertTrue(output.contains("Code loaded successfully!"));
    }

    /// Scenario 2: player has no previously saved game
    @Test
    void testNoSavedGame() {
        Player player = new Player("empty", true);
        Game game = new Game(player, true);

        outputStream.reset();  // clear output
        game.loadCode(); // attempts to load "empty" saved code

        String output = outputStream.toString(); // capture output

        assertTrue(output.contains("No saved code found"));
    }

    /// Scenario 3: error loading previously saved game (corrupt file)
    @Test
    void testCorruptSavedCode() throws IOException { // test throws exception
        String corruptedData = "corruptdata"; // save normally username,code here there is no comma

        // manually overwrites csv file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("savedCodes.csv"))) {
            writer.write(corruptedData);
        }

        Player player = new Player("corruptdata", true);
        Game game = new Game(player, true);

        outputStream.reset(); // reset console capture
        game.loadCode(); // load broken save

        String output = outputStream.toString(); // capture output

        assertTrue(output.contains("No saved code found")
                || output.contains("Error"));
    }




    ///ACCEPTANCE Criteria 5:
    /// "As a player I want to be able to show the solution so I can see a deciphered secret code for a code I can't decipher"

    // Scenario 1: Player shows the solution
    @Test
    void showSolution(){
        Player player = new Player("ShowSolutionTest", true);
        Game game = new Game(player, true);

        NumbersCode code = new NumbersCode(); // manually create new number code
        code.setCode("12345678"); // manually set code
        game.secretCode = code;

        System.setIn(new ByteArrayInputStream("v\n".getBytes())); // simulate user entering v to show solution
        Game.scanner = new Scanner(System.in); // reinitialize scanner to use simulated input

        outputStream.reset(); // reset output capture
        game.playGame(); // start game

        String output = outputStream.toString(); // capture output

        assertTrue(output.contains("Solution: 12345678"), "solution should be displayed");
        assertTrue(output.contains("Returning to lobby..."), "should return to lobby after solution shown");
    }

    /// ACCEPTANCE CRITERIA 6:
    /// "As a player I want to store my player name so the software can track my game play statistics "

    // Scenario 1: save statistics
    @Test
    void testSavedOnExit() {
        Player player = new Player("Martin", true);
        Game game = new Game(player, true);

        game.secretCode = new NumbersCode(); // manually create number code
        game.secretCode.setCode("4321"); // manually set code

        // manually increment stats
        player.incrementCodesAttempted();
        player.incrementCodesDeciphered();
        player.updateBulls(2);
        player.updateCows(1);
        player.incrementTotalNumGuesses();

        outputStream.reset(); // reset console capture
        player.saveDetails();

        String output = outputStream.toString(); // capture output

        // test that the players stats have been successfully saved
        assertTrue(output.contains("Player stats saved for "));
        File file = new File("playerDetails.csv");
        assertTrue(file.exists());
    }



    /// ACCEPTANCE CRITERIA 7:
    /// "As a player I want the software to track the number of secret codes I have successfully deciphered":

    // Scenario 1: secret code successfully deciphered
    @Test
    void testDecipheredCode() {
        Player player = new Player("Martin", true);
        Game game = new Game(player, true);

        game.secretCode = new NumbersCode(); // manually create number code
        game.secretCode.setCode("56789234"); // manually set code
        game.enterGuess("56789234"); // correct guess

        assertEquals(1, player.getTotalCodesDeciphered()); // update codes dechiphered
    }

    /// Scenario 2: secret code unsuccessfully deciphered
    @Test
    void testUnsuccessfulDecipher() {
        Player player = new Player("Martin", true);
        Game game = new Game(player, true);

        game.secretCode = new NumbersCode(); // manaully create number code
        game.secretCode.setCode("12345678"); // manually set code
        game.enterGuess("12345679"); // wrong guess

        assertEquals(0, player.getTotalCodesDeciphered()); // codes dechipred not updated
        assertEquals(1, player.getTotalNumGuesses()); // total guesses updated
    }


    /// ACCEPTANCE CRITERIA 8:
    /// "As a player I want the software to track the number of secret codes I have attempted to decipher so I can see how many I’ve attempted "

    /// Scenario 1: new secret code displayed
    @Test
    void testTrackAttemptedCodes() {
        Player player = new Player("Martin", true);
        Game game = new Game(player, true);

        game.secretCode = new NumbersCode(); // manually create number code
        player.incrementCodesAttempted(); // manually increment attempted codes

        assertEquals(1, player.getTotalCodesAttempted()); // attempted codes updated
    }

    /// Scenario 2: secret code loaded
    @Test
    void testLoadedCodeDoesNotIncrementAttempts() {
        Player player = new Player("Martin", true);
        Game game = new Game(player, true);

        NumbersCode code = new NumbersCode(); // manually create number code
        code.setCode("4321"); // manually set code
        game.secretCode = code; // assign code to games current code

        System.setIn(new ByteArrayInputStream("".getBytes()));

        game.saveCode();

        int attemptsBefore = player.getTotalCodesAttempted(); // capture num of attempts before

        outputStream.reset(); // reset output
        game.loadCode();

        assertEquals(attemptsBefore, player.getTotalCodesAttempted()); // checks codes attempted before and after are the same
    }


    /// ACCEPTANCE CRITERIA 9:
    /// "As a player I want the software to track the number of bulls and cows I have guessed so I can see how accurate I am as a percentage of my total number of guesses"


    /// Scenario 1: guess contains 1 or more bulls
    @Test
    void test1orMoreBulls() {
        Player player = new Player("Martin", true);
        Game game = new Game(player, true);

        game.secretCode = new LettersCode(); // manually create new number code
        game.secretCode.setCode("industry"); // manually set code
        game.enterGuess("abdcefgh"); // 1 bull

        assertEquals(1, player.getTotalNumberOfBulls()); // bulls updated
        assertEquals(1, player.getTotalNumGuesses()); // total guesses updated
    }

    /// Scenario 2: guess contains no bulls
    @Test
    void testNoBulls() {
        Player player = new Player("Martin", true);
        Game game = new Game(player, true);

        game.secretCode = new LettersCode(); // manually create number code
        game.secretCode.setCode("industry"); // manually set code
        game.enterGuess("abcefghj"); // 0 bulls

        assertEquals(0, player.getTotalNumberOfBulls());  // bulls  not updated
        assertEquals(1, player.getTotalNumGuesses()); // total guesses updated
    }


    /// Scenario 3: guess contains 1 or more cows
    @Test
    void test1orMoreCows() {
        Player player = new Player("Martin", true);
        Game game = new Game(player, true);

        game.secretCode = new LettersCode(); // manually create number code
        game.secretCode.setCode("industry"); // manually set code
        game.enterGuess("abcdefgh"); // 1 cow

        assertEquals(1, player.getTotalNumberOfCows()); // cows updated
        assertEquals(1, player.getTotalNumGuesses()); // total guesses updated
    }

    /// Scenario 4: guess contains no cows
    @Test
    void testGuessWithNoCows() {
        Player player = new Player("Martin", true);
        Game game = new Game(player, true);

        game.secretCode = new LettersCode(); // manually create number code
        game.secretCode.setCode("industry"); // manually set code
        game.enterGuess("abcefghj"); // no cows

        assertEquals(0, player.getTotalNumberOfCows()); // cows not updated
        assertEquals(1, player.getTotalNumGuesses()); // total guesses updated
    }

    /// Scenario 5: guess contains 1 or more bulls or 1 or more cows
    @Test
    void testGuessWithBullsAndCows() {
        Player player = new Player("Martin", true);
        Game game = new Game(player, true);

        game.secretCode = new LettersCode(); // manually create number code
        game.secretCode.setCode("industry"); // manually set code
        game.enterGuess("iabcndef"); // 1 bull, 2 cows

        assertEquals(1, player.getTotalNumberOfBulls()); // bulls updated
        assertEquals(2, player.getTotalNumberOfCows()); // cows updated
        assertEquals(1, player.getTotalNumGuesses()); // total guesses updated
    }

    /// ACCEPTANCE CRITERIA 10:
    /// "As a player I want to display my game play statistics so I can see my performance"


    // Scenario 1: player hasn’t played any games
    @Test
    void testDisplayStatsNoGames() {
        Player player = new Player("Martineee", true); // must be a player with empty stats
        Game game = new Game(player, true);

        game.currentPlayer.displayStats(); // gets stats of current player

        String output = outputStream.toString();// capture output

        // stats shouldn't be displayed if there are no stats for player
        assertFalse(output.contains("Total Number of Bulls: 0"));
        assertFalse(output.contains("Percentage of Guesses Bulls: 0")); // shouldn't contain percentage as would be dividing by 0
        assertFalse(output.contains("Total Number of Cows: 0"));
        assertFalse(output.contains("Percentage of Guesses Cows: 0"));
        assertFalse(output.contains("Total Number of Codes Attempted: 0"));
        assertFalse(output.contains("Total Number of Codes Deciphered: 0"));
        assertFalse(output.contains("Total Number of Guesses: 0"));
    }

    // Need to add error message if player tries to display stats wihtout any saved data


    /// Scenario 2: player has played at least one game
    @Test
    void testDisplayStats() {
        Player player = new Player("Martin", true);
        Game game = new Game(player, true);

        // simulating player having played a game before
        player.incrementCodesAttempted();
        player.incrementCodesDeciphered();
        player.updateBulls(4);
        player.updateCows(0);
        player.incrementTotalNumGuesses();

        outputStream.reset(); // reset output

        player.displayStats();

        String output = outputStream.toString(); // capture output


        // checks stats are updated correctly
        assertTrue(output.contains("Total Number of Codes Attempted: 1"));
        assertTrue(output.contains("Total Number of Codes Deciphered: 1"));
        assertTrue(output.contains("Total Number of Bulls: 4"));
        assertTrue(output.contains("Total Number of Cows: 0"));
        assertTrue(output.contains("Total Number of Guesses: 1"));
    }

    /// ACCEPTANCE CRITERIA 11:
    /// "As a player I want to load my details so I can track my game play statistics "

    // Scenario 1: player details loaded
    @Test
    void testLoadExistingPlayerDetails() {
        Player player = new Player("Martin", true);

        player.incrementCodesAttempted(); // increment attempted codes

        player.saveDetails();

        Player loadedPlayer = new Player("Martin");
        assertTrue(loadedPlayer.getTotalCodesAttempted() > 0); // checks loaded stats show at least 1 code attempted
    }

    /// Scenario 2: error loading player details
    @Test
    void testNewPlayerCreationOnMissingDetails() {
        // Player should have a deciphered code, but should be error with file reading
        String unknownPlayer = "Martine";

        outputStream.reset(); // reset output

        System.setIn(new ByteArrayInputStream("y\n".getBytes()));
        Player loadedPlayer = new Player(unknownPlayer); // trigger loadDetails()

        String output = outputStream.toString(); // capture output

        assertTrue(output.contains("No saved data found for " + unknownPlayer)); // checks correct error message shows
        Player.deletePlayerFromFile("Martine"); // delete player from file
    }


    /// Scenario 3: Error loading player, they don't exist

    @Test
    void testCreateNewPlayerIfNotFound() {
        String missingPlayer = "Steve";

        outputStream.reset();
        System.setIn(new ByteArrayInputStream("y\n".getBytes())); // Simulate typing "y"

        Player unknownPlayer = new Player(missingPlayer); // trigger loadDetails()

        String output = outputStream.toString();

        // Match exact formatting of the output
        assertTrue(output.contains("Player " + missingPlayer + " not found"));
        assertTrue(output.contains("No saved data found for " + missingPlayer + ". Would you like to create a new profile? (y/n)"));

        Player.deletePlayerFromFile(missingPlayer); // delete player from file
    }

    /// ACCEPTANCE CRITERIA 12: "As a player I want to be able to see the top 10 scores for number of successfully deciphered codes:"
    /// Scenario 1: Player wants to see the top 10 players ordered by proportion of successfully deciphered codes:
    @Test
    void testViewLeaderboard(){
        Player player = new Player("testLeaderBoard", true);

        // set stats
        player.incrementCodesAttempted();
        player.incrementCodesDeciphered();
        player.saveDetails(); // save stats

        Game game = new Game(player, true);

        outputStream.reset(); // clear output

        game.currentPlayer.top10Players(); // display leaderboard
        String output = outputStream.toString(); // capture output

        assertTrue(output.contains("Top 10 Players by Codes Deciphered"), "leader board output should be displayed");
        assertTrue(output.contains("testLeaderBoard"), "player name should appear in leaderboard");
        assertEquals(1, game.currentPlayer.getTotalCodesAttempted(), "attempted code should be 1");
        assertEquals(1, game.currentPlayer.getTotalCodesDeciphered(), "dechipred codes should be 1");


        Player.deletePlayerFromFile("testLeaderBoard");
    }

   /// Scenario 2: No Player stats have been stored
   @Test
   void testLeaderBoardNoPlayerStats(){
        //Ensure even whitespace is cleared from file
       Player player = new Player("test", true);
       Game game = new Game(player, true);

       outputStream.reset();
       game.currentPlayer.top10Players(); // uses default file path

       String output = outputStream.toString(); // capture output


       assertTrue(output.contains("Top 10 Players by Codes Deciphered"), "header should still be displayed");
       assertFalse(output.contains("|  1  |"), "no player stats should be in file when empty");

   }


    /// test display leaderboard with invalid file path
    @Test
    void testViewInvalidFileLeaderboard(){

        Player player = new Player("fraser", true);
        Game game = new Game(player, true);

        outputStream.reset();
        game.currentPlayer.top10Players("noFile.csv"); // try and call file that doesnt exist

        String output = outputStream.toString();
        assertTrue(output.contains("Error: No player stats file found!"),"should show error - no file exists"); // ensure correct error showed when no file exists

    }

    ///ACCEPTANCE CRITERIA 13: " As a player I want to be able to get a hint for a letter/number, so I can decipher the secret code"
    /// Scenario 1: first hint
    @Test
    void firstHint(){
        Player player = new Player("hintTest", true);
        Game game = new Game(player, true);


        NumbersCode code = new NumbersCode(); // manually create new number code
        code.setCode("12345678"); // manually set code
        game.secretCode = code;


        player.lastGuess = "87654321"; // simulate guess with 0 bulls


        outputStream.reset();
        game.getHint(player.lastGuess); // first hint

        String output = outputStream.toString(); // capture output


        assertTrue(output.contains("Hint:"), "hint should be shown");
    }

    /// Scenario 2: hints have previously been asked for:
    @Test
    void testPreviousHintsGiven(){
        Player player = new Player("multipleHintTest", true);
        Game game = new Game(player, true);


        NumbersCode code = new NumbersCode(); // manually create number code
        code.setCode("12345678"); // manually set code
        game.secretCode = code;

        player.lastGuess = "87654321"; // simulate guess with 0 bulls

        game.getHint(player.lastGuess); // first hint
        String output1 = outputStream.toString();

        player.lastGuess = "87654321";
        game.getHint(player.lastGuess);
        String output2 = outputStream.toString();

        // this is not reliable as the hints are random - possible to get the same one twice, although unlikely for guess without bulls:
        assertFalse(output1.equals(output2));
        // note - still passes with cowsay although, might be easier to remove it for Martins examination...?
    }
}


