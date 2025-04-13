import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Math.round;
import static java.lang.System.exit;

class Player {
    private String username;
    private int totalNumberOfBulls;
    private int totalNumberOfCows;
    private int totalCodesAttempted;
    private int totalCodesDeciphered;
    private int totalNumGuesses;

    public String lastGuess;

    private static final String PLAYER_FILE_PATH = "playerDetails.csv"; // playerDetails file - dont change

    // Standard constructor
    public Player(String username) {
        this.username = username;
        loadDetails();
    }

    // testing constructor
    public Player(String username, boolean testMode) {
        this.username = username;
        if (!testMode) {
            loadDetails();
        } else {
            // If we are in test mode, reset
            this.totalNumberOfBulls = 0;
            this.totalNumberOfCows = 0;
            this.totalCodesAttempted = 0;
            this.totalCodesDeciphered = 0;
            this.totalNumGuesses = 0;
        }
    }

    // method - saves player details to file corresponding file
    public void saveDetails() {
        List<String[]> players = new ArrayList<>(); // holds player data from file

        boolean playerFound = false;

        File file = new File(PLAYER_FILE_PATH);

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) { // read each line in file
                    String[] data = line.split(","); // spilt using comma to seperate

                    if (data[0].equals(username)) {   // if line belongs to current player

                        //update stats
                        data[1] = String.valueOf(totalNumberOfBulls);
                        data[2] = String.valueOf(totalNumberOfCows);
                        data[3] = String.valueOf(totalCodesAttempted);
                        data[4] = String.valueOf(totalCodesDeciphered);
                        data[5] = String.valueOf(totalNumGuesses);
                        playerFound = true;
                    }
                    players.add(data); // add data back to list
                }
            } catch (IOException e) {
                System.out.println("Error reading player details: " + e.getMessage());
            }
        }
        // must be new player:
        if (!playerFound) {
            players.add(new String[]{username,
                    String.valueOf(totalNumberOfBulls),
                    String.valueOf(totalNumberOfCows),
                    String.valueOf(totalCodesAttempted),
                    String.valueOf(totalCodesDeciphered),
                    String.valueOf(totalNumGuesses)});
        }
        // write all player back to file 
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PLAYER_FILE_PATH))) {
            for (String[] data : players) {
                bw.write(String.join(",", data));
                bw.newLine();
            }
            System.out.println("Player stats saved for " + username);
        } catch (IOException e) {
            System.out.println("Error writing player details: " + e.getMessage());
        }
    }

    // method - loads details from player file for specified player:
    public void loadDetails() {
        File file = new File(PLAYER_FILE_PATH);
        boolean playerFound = false;
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                // read data
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data[0].equals(username)) {
                        try{
                            this.totalNumberOfBulls = Integer.parseInt(data[1]);
                            this.totalNumberOfCows = Integer.parseInt(data[2]);
                            this.totalCodesAttempted = Integer.parseInt(data[3]);
                            this.totalCodesDeciphered = Integer.parseInt(data[4]);
                            this.totalNumGuesses = Integer.parseInt(data[5]);
                            System.out.println("Data successfully loaded for player " + username);
                            return;
                        } catch (ArrayIndexOutOfBoundsException aioobe) {
                            System.out.println("Error reading player details: " + username);
                            System.out.println("Potentially Corrupt Data!");
                            System.out.println("Exiting....");
                            exit(0);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading player details file" + e.getMessage());
            }
        }
        System.out.println("Player " + username + " not found");

        // Player is not found -> new player
        if (!playerFound) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("No saved data found for " + username + ". Would you like to create a new profile? (y/n)");
            String response = scanner.nextLine().toLowerCase();

            // if user wants to create new profile -> initalise:
            if (response.equals("y")) {
                System.out.println("Creating a new profile for " + username + "...");
                this.totalNumberOfBulls = 0;
                this.totalNumberOfCows = 0;
                this.totalCodesAttempted = 0;
                this.totalCodesDeciphered = 0;
                this.totalNumGuesses = 0;
                saveDetails();
            } else if (response.equals("n")) {
                // player doesn't want to create new profile -> exit game
                System.out.println("No new profile made!");
                System.out.println("Exiting...");
                exit(1);
            } else {
                // handle error
                System.out.println("Error creating a new profile for " + username + ". Exiting .");
                exit(1);
            }
        }
    }

    // method - displays stats for given player
    public void displayStats() {
        if(totalCodesAttempted > 0) {
            System.out.println("Player: " + username);
            System.out.println("Total Number of Bulls: " + totalNumberOfBulls);
            // dont display percentage if dividing by zero:
            if (totalNumberOfBulls > 0) {
                System.out.println("Percentage of Guesses Bulls: " + round(((double) totalNumberOfBulls / (double) totalNumGuesses) * 100) + "%");
            }
            System.out.println("Total Number of Cows: " + totalNumberOfCows);
            // same as above
            if (totalNumberOfCows > 0) {
                System.out.println("Percentage of Guesses Cows: " + round((((double) totalNumberOfCows / (double) totalNumGuesses) * 100)) + "%");
            }
            System.out.println("Total Number of Codes Attempted: " + totalCodesAttempted);
            System.out.println("Total Number of Codes Deciphered: " + totalCodesDeciphered);
            System.out.println("Total Number of Guesses: " + totalNumGuesses);
        }else{
            System.out.println("No saved data found for " + username + "! ");
            System.out.println("Exiting...");
        }
    }

    // method - display list of top 10 players by the most codes deciphered:
    public void top10Players() {
        List<String[]> players = new ArrayList<>();
        File file = new File(PLAYER_FILE_PATH);
        if (!file.exists()) {
            System.out.println("Error: No player stats file found!");
            return;
        }

        // read file and add the players to the array list
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                players.add(data);
            }
        } catch (IOException e) {
            System.out.println("Error reading player details: " + e.getMessage());
            return;
        }
        if (players.isEmpty()){
            System.out.println("No valid player stats to display");
            return;
        }

        // sort players into asc order:
        players.sort((a, b) -> Integer.parseInt(b[4]) - Integer.parseInt(a[4]));

        // format nicely in table
        System.out.println("\nTop 10 Players by Codes Deciphered:\n");
        System.out.println("+------+-----------------+-------------------+");
        System.out.printf("| %-4s | %-15s | %-17s |\n", "Rank", "Player", "Deciphered Codes");
        System.out.println("+------+-----------------+-------------------+");

        // print data with padding
        for (int i = 0; i < Math.min(10, players.size()); i++) {
            String[] playerData = players.get(i);
            System.out.printf("| %-4d | %-15s | %-17s |\n", i + 1, playerData[0], playerData[4]);
        }
        System.out.println("+------+-----------------+-------------------+");
    }

    // OVERLOADED - used for testing allows to change specified file path
    public void top10Players(String filePath) {
        List<String[]> players = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Error: No player stats file found!");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5) {
                    players.add(data);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading player details: " + e.getMessage());
            return;
        }

        players.sort((a, b) -> Integer.parseInt(b[4]) - Integer.parseInt(a[4]));

        System.out.println("\nTop 10 Players by Codes Deciphered:\n");
        System.out.println("+------+-----------------+-------------------+");
        System.out.printf("| %-4s | %-15s | %-17s |\n", "Rank", "Player", "Deciphered Codes");
        System.out.println("+------+-----------------+-------------------+");

        for (int i = 0; i < Math.min(10, players.size()); i++) {
            String[] playerData = players.get(i);
            System.out.printf("| %-4d | %-15s | %-17s |\n", i + 1, playerData[0], playerData[4]);
        }

        System.out.println("+------+-----------------+-------------------+");
    }


    // method - removes player from file overwrites original with everything apart from one we want to delete - mainly used for testing
    public static void deletePlayerFromFile(String username) {
        File file = new File(PLAYER_FILE_PATH);
        if (!file.exists()) return;

        try {
            StringBuilder filtered = new StringBuilder(); // holds all lines except ones to be deleted

            BufferedReader reader = new BufferedReader(new FileReader(file)); // open file
            String line;


            while ((line = reader.readLine()) != null) { // read each line of file
                if (!line.startsWith(username + ",")) { // if line doesn't start with username we want to delete
                    filtered.append(line).append(System.lineSeparator()); // keep it
                }
            }
            reader.close(); // close reader

            BufferedWriter writer = new BufferedWriter(new FileWriter(file)); // open again for overwriting

            writer.write(filtered.toString()); // write lines back into file
            writer.close(); // close writer

            System.out.println("Deleted player " + username + " from file.");

        } catch (IOException e) {
            System.out.println("Error deleting player from file: " + e.getMessage());
        }
    }

    public void updateCows(int cows) { this.totalNumberOfCows += cows; }
    public void updateBulls(int bulls) { this.totalNumberOfBulls += bulls; }
    public void incrementCodesAttempted() { totalCodesAttempted++; }
    public void incrementCodesDeciphered() { totalCodesDeciphered++; }
    public void incrementTotalNumGuesses() { totalNumGuesses++; }

    public String getUsername() { return username; }
    public int getTotalCodesAttempted() { return totalCodesAttempted; }
    public int getTotalCodesDeciphered() { return totalCodesDeciphered; }
    public int getTotalNumGuesses() { return totalNumGuesses; }
    public int getTotalNumberOfBulls() { return totalNumberOfBulls; }
    public int getTotalNumberOfCows() { return totalNumberOfCows; }
}
