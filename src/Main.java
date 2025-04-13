import java.util.*;

public class Main {
    // Class in which we call Game() from - Starts the Game
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        Player player = new Player(username);
        Game game = new Game(player);


        game.lobby();
        scanner.close();
    }
}