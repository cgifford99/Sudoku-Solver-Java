package sudoku;

import java.util.Random;
import java.util.Arrays;

public class SudokuSolver {

    public static void main(String[] args) {
        /* How are we setting this up?
         *    Is a UI necessary?
         *    What features does it have?
         *
         * Two main functions-
         *   Sudoku board generator (It either generates a new completed board or partially filled board
         *                           with specified number of values.)
         *   Sudoku solver (It will take a board inputted by the user and solves it.)
         *
         * Step 1: 9x9 Array Generator - Be able to manage arrays and create random numbers.
         *                               Print the board with correct formatting in the console.
         * Step 2: Sudoku Generator Algorithm -  Be able to "teach" (not A.I. although it'd be cool) the program the rules
         *                                       of sudoku and generate a solved board.
         * Step 3: Sudoku Solver Algorithm - Be able to solve any 9x9 sudoku board based on sudoku rules.
         * Step 4: Error catching implementation - You know what it is.
         * Step 5: Misc. Extra Features - Being able to solve larger boards. Generate Win32 application of sorts.
         *                                Java probably isn't the best for this.
         */

        final int BOARD_HORIZONTAL_LENGTH = 9;
        final int BOARD_VERTICAL_LENGTH = 9;

        Integer[][] boardInput = {{0, 0, 0, 6, 3, 5, 0, 0, 0},
                                  {0, 5, 0, 0, 0, 0, 0, 9, 0},
                                  {2, 0, 6, 7, 0, 9, 3, 0, 5},
                                  {0, 0, 2, 4, 6, 3, 8, 0, 0},
                                  {0, 1, 0, 0, 7, 0, 0, 6, 0},
                                  {0, 4, 0, 1, 0, 2, 0, 3, 0},
                                  {1, 0, 0, 0, 0, 0, 0, 0, 7},
                                  {0, 0, 0, 0, 2, 0, 0, 0, 0},
                                  {7, 0, 0, 0, 1, 0, 0, 0 ,3}};

        // Must supply sudoku.Board class with an initial 2d array. Java does not support optional params.
        // 'in' param will become more intuitive later. Currently, there is little use for it.
        Board board = new Board(BOARD_HORIZONTAL_LENGTH, BOARD_VERTICAL_LENGTH, "Input by User", boardInput);
        // Copy our board first instance of the board. Used for backtracking methods.
        for(int i = 0; i < board.getBoard().length; i++){
            for(int j = 0; j < board.getBoard().length; j++){
                boardInput[i][j] = board.getBoard()[i][j];
            }
        }
        board.setBoard(board.solve(boardInput));
        board.print(boardInput);
    }
}

class Board {
    private Integer[][] board;
    private Random rand = new Random();

    Board(int x, int y, String in, Integer[][] board) {
        if(in.contains("Input by User")) {
            if (board.length == 1) {
                // set board length and fill 0
                System.out.println("Null board. Filling zero.");
                this.board = new Integer[x][y];
                for (int i = 0; i < x; i++) {
                    Arrays.fill(board[i], 0);
                }
                this.setBoard(board);
            } else {
                System.out.println("Filled board. sudoku.Board ready to solve.");
                this.setBoard(board);
            }
        }
    }

    void setBoard(Integer[][] board){
        this.board = new Integer[board.length][board.length];
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board.length; j++){
                this.board[i][j] = board[i][j];
            }
        }
    }

    Integer[][] getBoard(){
        return this.board;
    }

    Integer[][] solve(Integer[][] board) {
        int x = board.length;
        int y = 0;
        for (Integer[] row : board) {
            y += 1;
        }

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                print(board);
                if(board[i][j] == 0) {
                    int randNum = generateNum(board, i, j);
                    if (randNum != -1) {
                        board[i][j] = randNum;
                    } else {
                        j = -1;
                        // reset board to initial values
                        // needs to reset other rows if needed
                        System.out.println("Reset");
                        System.out.println(Arrays.deepToString(this.board));
                        System.arraycopy(this.board[i], 0, board[i], 0, board.length);
                    }
                }
            }
        }
        return board;
    }

    private int generateNum(Integer[][] board, int i, int j) {
        char genNum;
        int numIdx;
        Integer[] row = getRow(board, i);
        Integer[] column = getColumn(board, i, j);
        Integer[] subsection = getSubsection(board, i, j);
        StringBuilder numAvail = new StringBuilder("123456789");

        for (int k = 0; k < row.length; k++) {
            if (row[k] != 0 && numAvail.toString().contains(row[k].toString())) {
                numIdx = numAvail.toString().indexOf(row[k].toString());
                numAvail.deleteCharAt(numIdx);
            }
            if (column[k] != 0 && numAvail.toString().contains(column[k].toString())) {
                numIdx = numAvail.toString().indexOf(column[k].toString());
                numAvail.deleteCharAt(numIdx);
            }
            if (subsection[k] != 0 && numAvail.toString().contains(subsection[k].toString())) {
                numIdx = numAvail.toString().indexOf(subsection[k].toString());
                numAvail.deleteCharAt(numIdx);
            }
            if(numAvail.length() == 0){
                break;
            }
        }
        int randIdx;
        if (numAvail.length() - 1 == -1) {
            return -1;
        }else if (numAvail.length() - 1 != 0){
            randIdx = rand.nextInt(numAvail.length());
            genNum = numAvail.charAt(randIdx);
            return Integer.parseInt(String.valueOf(genNum));
        }else{
            randIdx = 0;
            genNum = numAvail.charAt(randIdx);
            return Integer.parseInt(String.valueOf(genNum));
        }
    }

    private Integer[] getRow(Integer[][] board, int i) {
        return board[i];
    }

    private Integer[] getColumn(Integer[][] board, int i, int j) {
        Integer[] col = new Integer[board[i].length];
        for (int k = 0; k < col.length; k++) {
            col[k] = board[k][j];
        }
        return col;
    }

    private Integer[] getSubsection(Integer[][] board, int i, int j) {
        Integer[] subsection = new Integer[board[i].length];
        int subRowStart = (i / 3) * 3;
        int subRowEnd = subRowStart + 3;
        int subColStart = (j / 3) * 3;
        int subColEnd = subColStart + 3;
        int m = 0;

        for (int k = subRowStart; k < subRowEnd; k++) {
            for (int l = subColStart; l < subColEnd; l++) {
                subsection[m] = board[k][l];
                m++;
            }
        }
        return subsection;
    }

    void print(Integer[][] board) {
        System.out.println(" ----------------------- ");
        for (int i = 0; i < board.length; i++) {
            if (i == 3 || i == 6) {
                System.out.println("|-------+-------+-------|");
            }
            System.out.print("| ");
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j] + " ");
                if (j == 2 || j == 5 || j == 8) {
                    System.out.print("| ");
                }
            }
            System.out.println();
        }
        System.out.println(" ----------------------- ");
    }
}

