import java.util.*;

// NumbersCode Class generates a 8 digit, random, non-repeating value
class NumbersCode extends SecretCode {

    public NumbersCode() {
        generateCode();
    }

    // Generate unique number code:
    private void generateCode() {
        // create list of digits 0-9
        List<Integer> digits = new ArrayList<>();
        for (int i = 0; i <= 9; i++) digits.add(i);

        Collections.shuffle(digits); // randomly shuffle list

        decipheredCode = "";
        for (int i = 0; i < 8; i++) decipheredCode += digits.get(i); // add digits to code

        codeType = 'n'; // value represents Number code
    }
}