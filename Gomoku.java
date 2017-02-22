/**
 * Christopher Skinner
 * Date: February 15, 2017
 * CS 251
 * Section 001
 * Lab 3 - Gomoku
 */

import cs251.lab3.GomokuGUI;
import cs251.lab3.GomokuModel;

/**
 * Creates the game of Gomoku and implements the model which includes
 * the GUI written by Chenoweth. Also has a simple computer player that
 * can be used by just passing in the command line argument of "COMPUTER",
 * for no AI, pass the CMD line argument "NONE".
 */
public class Gomoku implements GomokuModel{
    private StringBuilder sb;
    private Square[][] gameArray;
    private int playerNum;
    private boolean isComputer;
    private Square[] players = {Square.RING, Square.CROSS};
    private Square currentPlayer;
    private Square winner;
    private int lastPlayerRow, lastPlayerCol;
    
    /**
     * Basic constructor for the game.
     *
     */
    public Gomoku() {
        this("computer");
    }

    /**
     * Overridden constructor to use the AI.
     */
    public Gomoku(String s) {
        // System.out.println("Cont");
        if (s.contains("COMPUTER")) {
            currentPlayer = players[1];
        } else {
            currentPlayer = players[0];
        }

        initGameArray();

        System.out.println("Begin Game");
    }

    /**
     * It's main, it just handles the creation of the object based on
     * the arguments passed in the CMD line.
     */
    public static void main( String[] args) {
        if ( args.length > 0) {
            Gomoku game = new Gomoku(args[0]);
            game.setComputerPlayer ( args [0]);
            GomokuGUI.showGUI ( game );
        } else {
            Gomoku game = new Gomoku();
            GomokuGUI.showGUI ( game );
        }
    }

    /**
    * Get the number of rows for the game board.
    *
    * @return DEFAULT_NUM_ROWS
    *
    */
    public int getNumRows() {
        return DEFAULT_NUM_ROWS;
    }

    /**
    * Get the number of columns for the game board.
    *
    * @return DEFAULT_NUM_COLS
    *
    */
    public int getNumCols() {
        return DEFAULT_NUM_COLS;
    }

    /**
    * Get the number in a line needed for a win.
    *
    * @return SQUARES_IN_LINE_FOR_WIN
    *
    */
    public int getNumInLineForWin() {
        return SQUARES_IN_LINE_FOR_WIN;
    }

    /**
    * Attempt a move at a given location on the board. This method is
    * called when the user clicks in the board. If the square is already
    * occupied, nothing about the state of the game changes. If however,
    * an empty square is clicked, then it should be filled with a value
    * representing the player currently in turn. On a failure, the turn
    * doesn't change, but that player gets to go again. Called from the GUI.
    *
    * @param row - 0-based row that was clicked
    * @param col - 0-based column that was clicked
    * 
    * @return State of this game after this moved occurred
    *
    */
    public Outcome playAtLocation(int row, int col) {
        // executes for the player that is current. Could be for 1 or 2
        // players.
        boolean flag = false;
        if(gameArray[row][col] == Square.EMPTY) {
            gameArray[row][col] = currentPlayer;
            lastPlayerRow = row - 2;
            lastPlayerCol = col;
            createBoardString();
            changePlayer();
        } else {
            System.out.println("Occupied Space");
            flag = true;
        }

        // Did the player that just play win?
        if (isWin()) {
            winner = currentPlayer;
            if (currentPlayer == Square.RING) {
                return Outcome.CROSS_WINS;
                
            } else {
                return Outcome.RING_WINS;
                
            }
        }

        // Check for full board
        int emptyCounter1 = 0;
        for (int i = 0; i < GomokuModel.DEFAULT_NUM_COLS; i++) {
            for (int j = 0; j < GomokuModel.DEFAULT_NUM_ROWS; j++) {
                if( gameArray[i][j] == Square.EMPTY) {
                    emptyCounter1++;
                }
            }
        }
        if (emptyCounter1 == 0) {
            return Outcome.DRAW;
        }

        // If there is a computer, the computer turn happens and creates the
        // board string.
        if (flag == false) {
            if (isComputer) {
                computerTurn();
                createBoardString();
                changePlayer();
            }
        }

        // Did the computer win?
        if (isWin()) {
            winner = currentPlayer;
            if (currentPlayer == Square.RING) {
                return Outcome.CROSS_WINS;
                
            } else {
                return Outcome.RING_WINS;
                
            }
        }

        // Check for draw and full board
        int emptyCounter = 0;
        for (int i = 0; i < GomokuModel.DEFAULT_NUM_COLS; i++) {
            for (int j = 0; j < GomokuModel.DEFAULT_NUM_ROWS; j++) {
                if( gameArray[i][j] == Square.EMPTY) {
                    emptyCounter++;
                }
            }
        }
        if (emptyCounter == 0) {
            return Outcome.DRAW;
        }

        return Outcome.GAME_NOT_OVER;
    }

    /**
     * This initializes a 2D array with the Square object
     */
    private void initGameArray() {

        // Creating new array and filling it with empty values
        gameArray = new Square[GomokuModel.DEFAULT_NUM_COLS][GomokuModel.DEFAULT_NUM_ROWS];
        
        for (int i = 0; i < GomokuModel.DEFAULT_NUM_COLS; i++) {

            for (int j = 0; j < GomokuModel.DEFAULT_NUM_ROWS; j++) {

                gameArray[i][j] = Square.EMPTY;

            }

        }

    }

    /**
    * Starts a new game, resets the game board to empty. Pick a random
    * player to go first. In the expected part of the program, you should
    * make it so that the player who won the last game gets to go first in
    * the next round. This method is called by the GUI whenever a new game
    * is supposed to be started, this includes before the first game.
    */
    public void startNewGame() {
        // System.out.println("StartNewGame");

        currentPlayer = players[1];
        if (isComputer) {
            // System.out.println("isComputer");
            if (currentPlayer == players[1]) {
                initGameArray();
                computerFirstTurn();
                createBoardString();
                changePlayer();
            } else {
                initGameArray();
                createBoardString();
                changePlayer();
            }
        } else {
            initGameArray();
            createBoardString();
            changePlayer();
        }

        // if (randomNum == 1) {

        //     initMovesArray();
        //     createBoardString();
        //     changePlayer();

        // } else {

        //     isComputer = true;
        //     initMovesArray();

        //     gameArray[(int)(Math.random()*DEFAULT_NUM_ROWS+1)]
        //     [(int)(Math.random()*DEFAULT_NUM_COLS+1)] = Square.CROSS;
        //     createBoardString();

        //     changePlayer();

        // }

    }

    /**
     * Takes the gameArray and uses String Builder to transfer it to
     * a string so it can be passed to the GUI.
     */
    private void createBoardString(){
        StringBuilder sbTemp = new StringBuilder();
        
        for (int i = 0; i < GomokuModel.DEFAULT_NUM_COLS; i++) {

            for (int j = 0; j < GomokuModel.DEFAULT_NUM_ROWS; j++) {

                //Getting the char value of square, then append using string builder.
                sbTemp.append(gameArray[i][j].toChar()); 
            }

            sbTemp.append("\n");
        }

        sb = sbTemp;
    }

    /**
    * Get a string representation of the board. The characters are given
    * by the Square.toChar method, so a hyphen in the string is an empty
    * square, an uppercase 'O' represents the ring, and uppercase 'X' is
    * the cross. Each line in the board is separated by a new line
    * character '\n'.
    *
    * @return String representation of the board
    */
    public String getBoardString() {

        return sb.toString();

    }

    /**
     * Switches players.
     *
     */
    private void changePlayer() {

        if (currentPlayer == players[0]) {

            currentPlayer = players[1];
        } else {

            currentPlayer = players[0];
        }
    }

    /**
     * Checks for win with the 4 different types of winning.
     *
     */
    private boolean isWin(){

        for (int i = 0; i < GomokuModel.DEFAULT_NUM_COLS; i++) {

            for (int j = 0; j < GomokuModel.DEFAULT_NUM_ROWS; j++) {

                if (gameArray[i][j].toChar() != Square.EMPTY.toChar()) {

                    if ( rightStraight(i, j) || downStraight(i, j) || 
                            leftDiagonal(i, j) || rightDiagonal(i, j)) {

                        System.out.println("win");
                        return true;
                    }   
                } 
            }
        }

        return false;
    }

    /**
     * Checks for win in a diagonal going down and right.
     *
     */
    private boolean rightDiagonal(int r, int c){
        Square sqTemp = gameArray[r][c];
        int counter = 0;

        for (int x = c, y = r; x < GomokuModel.DEFAULT_NUM_COLS &&
                     y < GomokuModel.DEFAULT_NUM_ROWS; x++,y++) {
            if (sqTemp.toChar() == gameArray[y][x].toChar()) {
                counter++;
            } else {
                break;
            }
        }
        
        return (counter >= GomokuModel.SQUARES_IN_LINE_FOR_WIN);
    }
    
    /**
     * Checks for win in a diagonal going down and left.
     *
     */
    private boolean leftDiagonal(int r, int c){
        Square sqTemp = gameArray[r][c];
        int counter = 0;
        
        if(r == 0 || c == 0) { return false; }
        for (int x = c, y = r; x < GomokuModel.DEFAULT_NUM_COLS &&
                 y < GomokuModel.DEFAULT_NUM_ROWS; x--,y++) {
            if (sqTemp.toChar() == gameArray[y][x].toChar()) {
                counter++;
            } else {
                break;
            }
        }
        
        return (counter >= GomokuModel.SQUARES_IN_LINE_FOR_WIN);
    }

    /**
     * Checks for win in a diagonal right.
     *
     */
    private boolean rightStraight(int r, int c){
        Square sqTemp = gameArray[r][c];
        int counter = 0;

        for (int i = c; i < GomokuModel.DEFAULT_NUM_ROWS; i++) {
            if (sqTemp.toChar() == gameArray[r][i].toChar()) {
                counter++;
            } else {
                break;
            }
        }
        
        return (counter >= GomokuModel.SQUARES_IN_LINE_FOR_WIN);
    }

    /**
     * Checks for win in a diagonal going down.
     *
     */
    private boolean downStraight(int r, int c){
        Square sqTemp = gameArray[r][c];
        int counter = 0;
        for (int i = r; i < GomokuModel.DEFAULT_NUM_COLS; i++) {
            if (sqTemp.toChar() == gameArray[i][c].toChar()) {
                counter++;
            } else {
                break;
            }
        }
        
        return (counter >= GomokuModel.SQUARES_IN_LINE_FOR_WIN);
    }

    /**
    * Configure whether a computer player will be used. At the very least,
    * recognize the following options
    * NONE - no computer player (the default)
    * COMPUTER - one of the players is the computer
    * It is permissible to have additional options if, for example, there
    * are multiple computer player implementations to choose from. If the
    * string is not a recognized computer player setting, print a message
    * to the console and use the default no player setting.
    *
    * @param String opponent
    */
    public void setComputerPlayer(String opponent) {
        if (opponent.equals("COMPUTER")) {
            isComputer = true;
        System.out.println("Computer Player Initalized");
        }
        
    }

    /**
     * The first turn has no player movement to go off of. So this
     * method just simply plays something random in the top left
     * of the board just to start the game.
     */
    private void computerFirstTurn() {
        int randomValue = (int)(Math.random()*10+1);
        gameArray[randomValue][randomValue] = Square.CROSS;
    }

    /**
     * Very simple AI that just reads off of the player's last turn
     * and plays right below/above it. Checks to see if it is off of the
     * board or is trying to play onto of another X or O and plays
     * accordingly.
     */
    private void computerTurn() {
        // System.out.println("Computer Turn");
        if (lastPlayerRow < 0) {
            lastPlayerRow += 2;
        }
        // gameArray[lastPlayerRow + 1][lastPlayerCol] = Square.CROSS;

        if(gameArray[lastPlayerRow + 1][lastPlayerCol] == Square.EMPTY) {
            gameArray[lastPlayerRow + 1][lastPlayerCol] = Square.CROSS;
            createBoardString();
        } else {
            System.out.println("Occupied Space");
            Outerloop:
            for (int i = 0; i < GomokuModel.DEFAULT_NUM_COLS; i++) {

                for (int j = 0; j < GomokuModel.DEFAULT_NUM_ROWS; j++) {
                    if (gameArray[i][j] == Square.EMPTY) {
                        gameArray[i][j] = Square.CROSS;
                        break Outerloop;
                    }
                }

            }
        }
    }

}

//QED
