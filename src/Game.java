import java.io.*;
import java.util.*;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;

class Game {
    public Player currentPlayer;
    public SecretCode secretCode;

    private static final String SAVED_CODES_FILE_PATH = "savedCodes.csv";

    private boolean testMode = false;


    static Scanner scanner = new Scanner(System.in);

    // constructor for normal gameplay
    public Game(Player p) {
        this.currentPlayer = p;
        printBanner();
    }

    // Constructor for testing
    public Game(Player p, boolean testMode) {
        this.currentPlayer = p;
        this.testMode = testMode;
    }

    public Game(Player p, SecretCode code) {
        this.currentPlayer = p;
        this.secretCode = code;
    }


    public void lobby() {
        while (true) {
            System.out.println("=================================================================");
            System.out.println("Enter Mode: ");
            System.out.println("Play Game - G");
            System.out.println("Load Code - C");
            System.out.println("View Player Stats - S");
            System.out.println("Top 10 Scores - B");
            System.out.println("Exit Game - Q");
            System.out.println("=================================================================");
            String input = scanner.nextLine();
            input = input.toLowerCase();

            switch (input) {
                case "g":
                    requestCode(); // ask user to choose code type
                    playGame();  // start game
                    break;
                case "c":
                    this.loadCode();  // load previously saved code
                    break;
                case "s":
                    currentPlayer.displayStats(); // show player stats
                    break;
                case "b":
                    currentPlayer.top10Players(); // show leaderboard
                    break;
                case "q":
                    System.out.println("Quitting lobby...");
                    currentPlayer.saveDetails();  // save stats before exiting
                    System.out.println("Goodbye!");
                    return; // exits
                default:
                    System.out.println("Invalid Mode: Try Again");
            }
        }
    }

    // main game loop where user enters guesses
    public void playGame() {
        System.out.println("\nEnter Q to Quit, S to save code, V to Show solution or H to get Hint\n");
        while (true) {
            System.out.print("Enter your guess: ");
            String guess = scanner.nextLine();
            guess = guess.toLowerCase();
            if (getSecretCode().isEmpty()) {
                System.out.println("Error With Game Code!");
                return;
            }
            if (guess.equals("q")) {
                System.out.println("Quitting game...");
                return;
            } else if (guess.equals("v")) {
                System.out.println("Solution: " + getSecretCode());
                System.out.println("Better luck next time!");
                System.out.println("Returning to lobby...");
                return;
            } else if (guess.equals("h")){
                if(currentPlayer.lastGuess == null || currentPlayer.lastGuess.isEmpty()) { // player doesn't have previous guess
                    System.out.println("No guesses have yet been entered!");
                    System.out.println("Give it a go first!");
                }else {
                    getHint(currentPlayer.lastGuess); // prints hint from method
                }
            } else if (guess.equals("test")){
                System.out.println(getSecretCode());
            } else if (guess.equals("s")) {
                this.saveCode();
                return;
            } else {
                enterGuess(guess);
            }
            if (guess.equals(getSecretCode())) {
                return;
            }
        }
    }

    // Loops through the user guess and compares to correct code, add non-matching values to array -->
    // choose something from that user array to give as a hint to the user:

    // 'test' 'v' etc. aren't valid guesses so work fine --> will get hints for last valid guess
    public void getHint(String lastGuess) {
        char[] hints = new char [secretCode.decipheredCode.length()]; // store possible hint characters
        int[] hintsPositions = new int [secretCode.decipheredCode.length()]; // stores index of hint in the actual secret code
        int hintCount = 0; // number of valid hints available

        // convert last guess & secret code into arrays for easy comparison
        char [] lastGuessArray = lastGuess.toCharArray();
        char [] secretCodeArray = secretCode.decipheredCode.toCharArray();


        for(int i = 0; i < secretCodeArray.length; i++){ // loop through each position in guess
            if(lastGuessArray[i] != secretCodeArray[i]){ // if character at position doesnt match codes character
                hints[hintCount] = secretCodeArray[i]; // add to hints list
                hintsPositions[hintCount] = i; // store position of correct character
                hintCount++; // increase number of available hints
            }
        }

        if (hintCount == 0){ // if no hints available
            System.out.println("Oops! No Hints Available!"); // display message to user
            return;
        }
        // Select random hint
        Random random = new Random();
        int index = random.nextInt(hintCount); // pick a valid value for hint
        char hint = hints[index]; // get the actual hint digit
        int hintPosition = hintsPositions[index];

        printCowsay("Hint: " + hint +" at position "+ (hintPosition+1)); // display hint
        //System.out.println("Hint: " + hint +" at position "+ (hintPosition+1));
    }

    public void saveCode() {
        Map<String, String> savedCodes = loadAllSavedCodes();
        String playerName = currentPlayer.getUsername();
        String codeToSave = secretCode.getCode();

        if (savedCodes.containsKey(playerName)) {
            System.out.println("You already have a saved code. Do you want to overwrite it? (y/n)");
            String response = scanner.nextLine().trim().toLowerCase();

            if (!response.equals("y")) {
                System.out.println("Save operation cancelled. Your previous code is kept.");
                return;
            }
        }
        savedCodes.put(playerName, codeToSave);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVED_CODES_FILE_PATH))) {
            for (Map.Entry<String, String> entry : savedCodes.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
            System.out.println("Code saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving code: " + e.getMessage());
        }
    }

    public void loadCode() {
        Map<String, String> savedCodes = loadAllSavedCodes(); // load saved codes into a map
        String playerName = currentPlayer.getUsername();

        // checks if player has previous saved code
        if (!savedCodes.containsKey(playerName)) {
            System.out.println("No saved code found for " + playerName + ".");
            return;
        }
        // checks if saved code is corrupted
        if (savedCodes.get(playerName).length() != 8 ) {
            System.out.println("An error occurred attempting to load code for: "+currentPlayer.getUsername());
            System.out.println("Potentially corrupt code!");
            System.out.println("Exiting...");
            return;

        } else {

            String loadedCode = savedCodes.get(playerName);
            secretCode = new SecretCode();
            secretCode.setCode(loadedCode);

            System.out.println("Code loaded successfully!");

            // if not in test mode start game using loaded code
            if (!testMode) {
                System.out.println("Starting a new game with the loaded code...");
                playGame();
            }
        }
    }

    private Map<String, String> loadAllSavedCodes() {
        Map<String, String> savedCodes = new HashMap<>();
        File file = new File(SAVED_CODES_FILE_PATH);

        // if no file exists return empty map
        if (!file.exists()) {
            return savedCodes;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) { // read each line from file
                String[] parts = line.split(","); // split line using comma to seperate

                if (parts.length == 2) { // if line has 2 parts (username and code)
                    savedCodes.put(parts[0], parts[1]); // add to map
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading save file: " + e.getMessage());
        }
        return savedCodes; // return map
    }

    public void requestCode() {

        while (true) { // loop until valid input

            // display code types to player
            System.out.println("\nChoose code type: ");
            System.out.println("1 - Numbers");
            System.out.println("2 - Letters\n");

            String codeType = scanner.nextLine(); // read user input

            if (codeType.equalsIgnoreCase("1")) { // number code option
                secretCode = new NumbersCode(); // generate number code
                System.out.println("Numbers code generated!");
                currentPlayer.incrementCodesAttempted(); // update codes attempted
                break;
            } else if (codeType.equalsIgnoreCase("2")) { // letter code option
                secretCode = new LettersCode(); // generate letter code
                System.out.println("Letters code generated!");
                currentPlayer.incrementCodesAttempted(); // update codes attempted
                break;
            } else { // invalid input
                System.out.println("Invalid code, please try again");
            }
        }
    }

    public boolean validateUserGuess(String guess) {
        if (guess.length() > 8) {
            System.out.println("Guess Too Long! Guesses Should Be of Length 8!");
        } else if (guess.length() < 8) {
            System.out.println("Guess Too Short! Guesses Should Be of Length 8!");
        } else if (getCodeType() == 'n' && !validateGuessType(guess, getCodeType())) {
            System.out.println("Code is of type number, please enter a 8 digit number");
        } else if (getCodeType() == 'l' && !validateGuessType(guess, getCodeType())) {
            System.out.println("Code is of type letter, please enter a 8 letter word");
        } else if (!uniqueLetters(guess)) {
            System.out.println("Guess must be made up of unique characters. Try again.");
        } else {
            return true;
        }
        return false;
    }

    public void enterGuess(String guess) {
        if (!validateUserGuess(guess)) { // validate user input
            return;
        } else {
            currentPlayer.lastGuess = guess; // set players last guess for hints

            String code = secretCode.getCode();
            int bulls = 0, cows = 0;

            for (int i = 0; i < guess.length(); i++) { // loop through each character of guess
                if (guess.charAt(i) == code.charAt(i)) { // if exact match (Bull)
                    bulls++;
                } else if (code.contains(String.valueOf(guess.charAt(i)))) { // match in wrong position (Cow)
                    cows++;
                }
            }
            // update player stats
            currentPlayer.updateBulls(bulls);
            currentPlayer.updateCows(cows);
            currentPlayer.incrementTotalNumGuesses();

            System.out.println("Bulls: " + bulls + ", Cows: " + cows);

            if (guess.equals(getSecretCode())) { // if guess exact match to code
                System.out.println("Congratulations! You deciphered the code!");

                currentPlayer.incrementCodesDeciphered(); // update dechiphered stat
                currentPlayer.saveDetails(); // save stats
            }
        }
    }

    private boolean validateGuessType(String guess, char codeType) {
        for (int i = 0; i < guess.length(); i++) { // loop through each character in guess
            if (codeType == 'n' && isDigit(guess.charAt(i))) { // if number code check character is digit
                continue;
            } else if (codeType == 'l' && (((int) guess.charAt(i)) > 96 && ((int) guess.charAt(i)) < 123)) { // if number code check chaarcter is letter
                continue;
            } else { // if doesnt match expected type
                return false;
            }
        }
        return true; // if all characters pass
    }

    public boolean uniqueLetters(String guess) {
        for (int i = 0; i < guess.length(); i++) { // loop through each character in guess
            for (int j = i + 1; j < guess.length(); j++) {  // compare character to characters that come after it
                if (guess.charAt(i) == guess.charAt(j)) { // if duplicate guess invlaid
                    return false;
                }
            }
        }
        return true;
    }

    public char getCodeType() {
        return secretCode.codeType;
    }

    public String getSecretCode() {
        return secretCode.getCode();
    }

    private void printCowsay(String message) {
        String border = "+-" + "-".repeat(message.length()) + "-+";
        String padding = "| " + message + " |";

        System.out.println(border);
        System.out.println(padding);
        System.out.println(border);

        // Cowsay ASCII Art
        System.out.println("        \\   ^__^");
        System.out.println("         \\  (oo)\\_______");
        System.out.println("            (__)\\       )\\/\\");
        System.out.println("                ||----w |");
        System.out.println("                ||     ||");
    }


    private void printBanner(){
        System.out.println("    ____        ____                        __  ______                  ");
        System.out.println("   / __ )__  __/ / /____   ____ _____  ____/ / / ____/___ _      _______");
        System.out.println("  / __  / / / / / / ___/  / __ `/ __ \\/ __  / / /   / __ \\ | /| / / ___/");
        System.out.println(" / /_/ / /_/ / / (__  )  / /_/ / / / / /_/ / / /___/ /_/ / |/ |/ (__  ) ");
        System.out.println("/_____/\\__,_/_/_/____/   \\__,_/_/ /_/\\__,_/  \\____/\\____/|__/|__/____/  ");
    }

}