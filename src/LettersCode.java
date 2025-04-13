import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.lang.System.exit;

// LettersCode class generates a random Isogram for the game code
class LettersCode extends SecretCode {

    private static final String FILE_PATH = "Isograms.txt"; // can't be changed

    public LettersCode() {
        generateCode();
    }

    // Generate letters code using isogram text file:
    protected void generateCode() {
        decipheredCode = "";
        Random random = new Random();
        try {
            List<String> words = Files.readAllLines(Paths.get(FILE_PATH)); // read all line in txt and stores them
            if (words.isEmpty()) {
                System.out.println("Error: No words in file."); // Make more professional - actual error
                System.out.println("Exiting...");
                exit(1);
            }
            decipheredCode = words.get(random.nextInt(words.size())); // select random word from list
        } catch (IOException io) {
            System.out.println("Error Reading File");
            System.out.println("Exiting...");
            exit(1);
        }
        codeType = 'l'; // value represents Letters code
    }


}
