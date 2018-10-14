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
        board.solve(boardInput);
        boardInput = board.getBoard();
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
                System.out.println("Filled board. Board ready to solve.");
                this.setBoard(board);
            }
        }
    }

    void setBoard(Integer[][] board){
        this.board = new Integer[board.length][board.length];
        for(int i = 0; i < board.length; i++){
            System.arraycopy(board[i], 0, this.board[i], 0, board.length);
        }
    }

    Integer[][] getBoard(){
        return this.board;
    }

    boolean solve(Integer[][] board) {
        int x = board.length;
        int y = 0;
        for (Integer[] row : board) {
            y += 1;
        }

        for (int row = 0; row < x; row++) {
            for (int col = 0; col < y; col++) {
                // if cell is empty, solve for it
                if(board[row][col] == 0) {
                    // iterate through all possible values
                    for (int num = 0; num <= x; num++) {
                        // check if value is valid
                        if (numValidation(board, row, col, num)) {
                            board[row][col] = num;
                            // using recursion, i'm not sure.....
                            if (solve(board)) {
                                return true;
                            }else{
                                board[row][col] = 0;
                            }
                        }
                    }
                    return false;
                }
            }
        }
        this.setBoard(board);
        return true;
    }

    private boolean numValidation(Integer[][] board, int i, int j, int num){
        // returns true only if our generated number is not within any row, column or subsection
        return (checkRow(board, i, num) && checkColumn(board, i, j, num) && checkSubsection(board, i, j, num));
    }

    private boolean checkRow(Integer[][] board, int i, int num) {
        return !Arrays.asList(board[i]).contains(num);
    }

    private boolean checkColumn(Integer[][] board, int i, int j, int num) {
        Integer[] col = new Integer[board[i].length];
        for (int k = 0; k < col.length; k++) {
            col[k] = board[k][j];
        }
        return !Arrays.asList(col).contains(num);
    }

    private boolean checkSubsection(Integer[][] board, int i, int j, int num) {
        Integer[] subsection = new Integer[board[i].length];
        int subRow = (i / 3) * 3;
        int subCol = (j / 3) * 3;
        int m = 0;

        for (int k = subRow; k < subRow + 3; k++) {
            for (int l = subCol; l < subCol + 3; l++) {
                subsection[m] = board[k][l];
                m++;
            }
        }
        return !Arrays.asList(subsection).contains(num);
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