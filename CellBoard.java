import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;


public class CellBoard {
    private JFrame frame;
    private JPanel boardPanel;
    private JButton[][] cells;
    private String[] playerNames;
    private int[] playerPositions;
    private int currentPlayerIndex;


    public CellBoard(String[] names) {
        frame = new JFrame("Cell Board");
        boardPanel = new JPanel(new GridLayout(5, 5));
        cells = new JButton[5][5];
        playerNames = names;
        playerPositions = new int[names.length];
        currentPlayerIndex = 0;


        initializeCells();
        setupFrame();
    }


    private void initializeCells() {
        Random rand = new Random();
        int blueCount = 0;
        int redCount = 0;
        int purpleCount = 0;
   
        // Create a list of available positions on the board
        List<Integer> availablePositions = new ArrayList<>();
        for (int i = 1; i <= 23; i++) {
            availablePositions.add(i);
        }
   
        // Place the blue, red, and purple squares randomly
        while ((blueCount < 5 || redCount < 5 || purpleCount < 2) && availablePositions.size() > 0) {
            int randomIndex = rand.nextInt(availablePositions.size());
            int position = availablePositions.get(randomIndex);
            int row = position / 5;
            int col = position % 5;
   
            cells[row][col] = new JButton();
            cells[row][col].setOpaque(true);
   
            // Randomly assign the color
            int color = rand.nextInt(10);
            if (color < 5 && blueCount < 5) {
                cells[row][col].setBackground(Color.BLUE);
                blueCount++;
            } else if (color < 9 && redCount < 5) {
                cells[row][col].setBackground(Color.RED);
                redCount++;
            } else if (purpleCount < 2) {
                cells[row][col].setBackground(new Color(128, 0, 128));
                purpleCount++;
            } else {
                cells[row][col].setBackground(Color.GREEN);
            }
   
            cells[row][col].setText(String.valueOf(position + 1));
            availablePositions.remove(randomIndex);
        }
   
        // Fill the remaining positions with green squares
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (cells[i][j] == null) {
                    cells[i][j] = new JButton();
                    cells[i][j].setOpaque(true);
                    cells[i][j].setBackground(Color.GREEN);
                    cells[i][j].setText(String.valueOf(i * 5 + j + 1));
                }
            }
        }
   
        // Set the label to the top right corner
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                cells[row][col].setHorizontalAlignment(SwingConstants.RIGHT);
                boardPanel.add(cells[row][col]);
            }
        }
    }
   
   
   
   
   
   
   
    private void setupFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.getContentPane().add(boardPanel);


        // Add key listener to the frame
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    rollDice();
                }
            }
        });


        // Set focusable to true, so that the frame can receive key events
        frame.setFocusable(true);


        frame.setVisible(true);
    }


    private void rollDice() {
    Random rand = new Random();
    int result = rand.nextInt(6) + 1;
    JOptionPane.showMessageDialog(frame, playerNames[currentPlayerIndex] + " rolled a " + result + "!");

    int previousPosition = playerPositions[currentPlayerIndex];
    int prevRow = previousPosition / 5;
    int prevCol = previousPosition % 5;
    cells[prevRow][prevCol].setText((prevRow * 5 + prevCol) + 1 + "");

    int row = prevRow;
    int col = prevCol;

    // Move player position based on the result
    for (int i = 0; i < result; i++) {
        col++;
        if (col == 5) {
            col = 0;
            row++;
        }
        if (row == 5) {
            row = 4;
            col = 4;
            break; // Stop moving if reached the end square
        }
    }

    playerPositions[currentPlayerIndex] = row * 5 + col;

    // Check if the player has landed on a purple square
    Color cellColor = cells[row][col].getBackground();
    if (cellColor.equals(new Color(128, 0, 128))) {
        handlePurpleSquare();
    } else {
        movePlayerPosition();
    }
}

private void movePlayerPosition() {
    int row = playerPositions[currentPlayerIndex] / 5;
    int col = playerPositions[currentPlayerIndex] % 5;
    cells[row][col].setText(playerNames[currentPlayerIndex]);

    Color cellColor = cells[row][col].getBackground();
    if (cellColor.equals(Color.RED)) {
        movePlayerBackward(1);
    } else if (cellColor.equals(Color.BLUE)) {
        movePlayerForward(1);
    } else {
        checkGameResult();
        currentPlayerIndex = (currentPlayerIndex + 1) % playerNames.length;
    }
}

   
  private void handlePurpleSquare() {
    // Create a new JFrame for the Rock-Paper-Scissors game
    JFrame rpsFrame = new JFrame("Rock-Paper-Scissors");

    // Set up the game interface, buttons, and logic
    JLabel resultLabel = new JLabel("Choose: Rock, Paper, or Scissors");
    JButton rockButton = new JButton("Rock");
    JButton paperButton = new JButton("Paper");
    JButton scissorsButton = new JButton("Scissors");

    // Add an ActionListener to the game buttons to handle the game result
    rockButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            handleGameResult("rock");
            rpsFrame.dispose();
        }
    });

    paperButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            handleGameResult("paper");
            rpsFrame.dispose();
        }
    });

    scissorsButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            handleGameResult("scissors");
            rpsFrame.dispose();
        }
    });

    // Set up the RPS frame layout
    rpsFrame.setLayout(new GridLayout(4, 1));
    rpsFrame.add(resultLabel);
    rpsFrame.add(rockButton);
    rpsFrame.add(paperButton);
    rpsFrame.add(scissorsButton);

    // Set up the RPS frame and make it visible
    rpsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    rpsFrame.setSize(400, 400);
    rpsFrame.setVisible(true);
}

private void handleGameResult(String playerChoice) {
    Random rand = new Random();
    int computerChoice = rand.nextInt(3); // 0 for rock, 1 for paper, 2 for scissors

    int playerChoiceInt;
    if (playerChoice.equalsIgnoreCase("rock")) {
        playerChoiceInt = 0;
    } else if (playerChoice.equalsIgnoreCase("paper")) {
        playerChoiceInt = 1;
    } else if (playerChoice.equalsIgnoreCase("scissors")) {
        playerChoiceInt = 2;
    } else {
        // Invalid choice
        JOptionPane.showMessageDialog(frame, "Invalid choice!");
        return;
    }

    // Determine the game result
    int result = (3 + playerChoiceInt - computerChoice) % 3;
    // 0 for a tie, 1 for player win, 2 for computer win

    if (result == 0) {
        JOptionPane.showMessageDialog(frame, "It's a tie!");
    } else if (result == 1) {
        JOptionPane.showMessageDialog(frame, "You win!");
        movePlayerForward(2);
    } else {
        JOptionPane.showMessageDialog(frame, "You lose!");
        movePlayerBackward(2);
    }
}


private void movePlayerForward(int steps) {
    int previousPosition = playerPositions[currentPlayerIndex];
    int prevRow = previousPosition / 5;
    int prevCol = previousPosition % 5;
    cells[prevRow][prevCol].setText(String.valueOf(previousPosition + 1));

    int newPosition = playerPositions[currentPlayerIndex] + steps;
    if (newPosition >= 25) {
        newPosition = 24;
    }
    playerPositions[currentPlayerIndex] = newPosition;

    // Check if the player has landed on a purple square
    Color cellColor = cells[newPosition / 5][newPosition % 5].getBackground();
    if (cellColor.equals(new Color(128, 0, 128))) {
        handlePurpleSquare();
    } else {
        updatePlayerPositionOnBoard();
        checkGameResult();
        currentPlayerIndex = (currentPlayerIndex + 1) % playerNames.length;
    }
}

private void movePlayerBackward(int steps) {
    int previousPosition = playerPositions[currentPlayerIndex];
    int prevRow = previousPosition / 5;
    int prevCol = previousPosition % 5;
    cells[prevRow][prevCol].setText(String.valueOf(previousPosition + 1));

    int newPosition = playerPositions[currentPlayerIndex] - steps;
    if (newPosition < 0) {
        newPosition = 0;
    }
    playerPositions[currentPlayerIndex] = newPosition;

    // Check if the player has landed on a purple square
    Color cellColor = cells[newPosition / 5][newPosition % 5].getBackground();
    if (cellColor.equals(new Color(128, 0, 128))) {
        handlePurpleSquare();
    } else {
        updatePlayerPositionOnBoard();
        currentPlayerIndex = (currentPlayerIndex + 1) % playerNames.length;
    }
}


private void updatePlayerPositionOnBoard() {
    int row = playerPositions[currentPlayerIndex] / 5;
    int col = playerPositions[currentPlayerIndex] % 5;
    cells[row][col].setText(playerNames[currentPlayerIndex]);
}

private void checkGameResult() {
    if (playerPositions[currentPlayerIndex] >= 24) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                cells[i][j].setText("");
            }
        }
        JOptionPane.showMessageDialog(frame, "Game Over! " + playerNames[currentPlayerIndex] + " wins!");

        // Reset player positions
        playerPositions = new int[playerNames.length];
    }
}


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        System.out.println("Welcome to the Player Names Program!");


        // Ask for the number of players
        int numberOfPlayers;
        do {
            System.out.print("Enter the number of players (2-4): ");
            numberOfPlayers = scanner.nextInt();
        } while (numberOfPlayers < 2 || numberOfPlayers > 4);


        // Create an array to store the player names
        String[] playerNames = new String[numberOfPlayers];


        // Ask for player names
        for (int i = 0; i < numberOfPlayers; i++) {
            System.out.print("Enter the name of player " + (i + 1) + ": ");
            playerNames[i] = scanner.next();
        }


        scanner.close();


        SwingUtilities.invokeLater(() -> {
            CellBoard cellBoard = new CellBoard(playerNames);
            cellBoard.handlePurpleSquare(); // Call the function to handle purple square after initializing the board
        });
    }
}
