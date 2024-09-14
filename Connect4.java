import java.util.Scanner;


public class Connect4 {

	private static final int ROWS = 6;
    private static final int COLUMNS = 7;
    private static final char EMPTY = '.';
    private static final char PLAYER1 = 'X';
    private static final char PLAYER2 = 'O';
    
    private static char[][] board = new char[ROWS][COLUMNS];
    
    public static void main(String[] args) {
        initializeBoard();
        printBoard();
        char currentPlayer = PLAYER1;
        boolean gameWon = false;
        
        while (!gameWon) {
            int column = getPlayerMove(currentPlayer);
            if (dropDisc(currentPlayer, column)) {
                printBoard();
                gameWon = checkWin(currentPlayer);
                
                if (gameWon) {
                    System.out.println("Player " + currentPlayer + " wins!");
                } else if (isBoardFull()) {
                    System.out.println("The game is a draw!");
                    break;
                }
                
                currentPlayer = (currentPlayer == PLAYER1) ? PLAYER2 : PLAYER1;
            }
        }
    }
    
    private static void initializeBoard() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                board[i][j] = EMPTY;
            }
        }
    }
    
    private static void printBoard() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    private static int getPlayerMove(char player) {
        Scanner scanner = new Scanner(System.in);
        int column;
        
        while (true) {
            System.out.println("Player " + player + ", enter the column (1-7) to drop your disc: ");
            column = scanner.nextInt() - 1;
            
            if (column >= 0 && column < COLUMNS && board[0][column] == EMPTY) {
                break;
            }
            
            System.out.println("Invalid move. Please try again.");
        }
        
        return column;
    }
    
    private static boolean dropDisc(char player, int column) {
        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][column] == EMPTY) {
                board[row][column] = player;
                return true;
            }
        }
        return false;
    }
    
    private static boolean checkWin(char player) {
        // Check horizontal, vertical, and diagonal wins
        return checkHorizontal(player) || checkVertical(player) || checkDiagonal(player);
    }
    
    private static boolean checkHorizontal(char player) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col <= COLUMNS - 4; col++) {
                if (board[row][col] == player && board[row][col + 1] == player &&
                    board[row][col + 2] == player && board[row][col + 3] == player) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean checkVertical(char player) {
        for (int col = 0; col < COLUMNS; col++) {
            for (int row = 0; row <= ROWS - 4; row++) {
                if (board[row][col] == player && board[row + 1][col] == player &&
                    board[row + 2][col] == player && board[row + 3][col] == player) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean checkDiagonal(char player) {
        // Check for diagonal (bottom-left to top-right) and (top-left to bottom-right) matches
        for (int row = 0; row <= ROWS - 4; row++) {
            for (int col = 0; col <= COLUMNS - 4; col++) {
                if (board[row][col] == player && board[row + 1][col + 1] == player &&
                    board[row + 2][col + 2] == player && board[row + 3][col + 3] == player) {
                    return true;
                }
            }
            for (int col = 3; col < COLUMNS; col++) {
                if (board[row][col] == player && board[row + 1][col - 1] == player &&
                    board[row + 2][col - 2] == player && board[row + 3][col - 3] == player) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean isBoardFull() {
        for (int i = 0; i < COLUMNS; i++) {
            if (board[0][i] == EMPTY) {
                return false;
            }
        }
        return true;
    }
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

