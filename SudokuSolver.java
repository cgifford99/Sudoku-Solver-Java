package sudoku;

import javax.swing.*;
import java.util.Arrays;
import java.util.concurrent.ConcurrentSkipListMap;

class SudokuSolver {

    static final int BOARD_WIDTH = 9;
    static final int BOARD_HEIGHT = 9;
    static Integer[][] boardInput = new Integer[BOARD_HEIGHT][BOARD_WIDTH];

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
    // Must supply sudoku. Board class with an initial 2d array. Java does not support optional params.
    // 'in' param will become more intuitive later. Currently, there is little use for it.

    //TODO: Determine unique solution or not before difficulty evaluation begins. It must have a certain number of givens beforehand.
}

@SuppressWarnings("WeakerAccess")
class Board{
    private Integer[][] board;
    private JTextField[][] tfCells = null;


    Board(int x, int y, String in, Integer[][] board) {
        // Input by User will take a board from up above. In this implementation it is 'boardInput'.
        if(in.contains("Input by User")) {
            if (board.length == 1) {
                // set board length and fill 0
//                System.out.println("Null board. Filling zero.");
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
        // Input by Program will be implemented soon. Too much college ;)
        // It will generate a board with various params and generate a random 'boardInput' (for reference).
    }

    void setBoard(Integer[][] board){
        this.board = new Integer[board.length][board.length];
        for(int i = 0; i < board.length; i++)
            System.arraycopy(board[i], 0, this.board[i], 0, board.length);
    }

    Integer[][] getBoard(){
        return this.board;
    }

    void setBoardIndex(int i, int j, Integer num){
        this.board[i][j] = num;
    }

    Integer getBoardIndex(int i, int j){
        return this.board[i][j];
    }

    // Using recursive techniques, the board is solved based on a previously entered/generated board.
    boolean solve(Integer[][] board) {
        int x = board.length;
        int y = 0;
        for (Integer[] row : board)
            y += 1;

        for (int row = 0; row < x; row++) {
            for (int col = 0; col < y; col++) {
                // if cell is empty, solve for it
                if(board[row][col] == 0) {
                    // iterate through all possible values in Sudoku
                    for (int num = 0; num <= x; num++) {
                        // check if value is valid
                        if (numValidation(board, row, col, num)) {
                            if(tfCells != null)
                                tfCells[row][col].setText(String.valueOf(num));
                            board[row][col] = num;
                            // solve board recursively
                            if (solve(board))
                                return true;
                            else
                                board[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        this.setBoard(board);
        return true;
    }



    boolean numValidation(Integer[][] board, int i, int j, int num){
        // returns true only if our generated number is not within any row, column or subsection
//        System.out.println("Row: " + i);
//        System.out.println("Col: " + j);
//        System.out.println("Num: " + num);
//        System.out.println("CheckRow: " + checkRow(board, i, num));
//        System.out.println("CheckCol: " + checkColumn(board, i, j, num));
//        System.out.println("CheckSub: " + checkSubsection(board, i, j, num));
//        this.print(board);

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

    // Generates all values within a specified 3x3 square and returns a boolean dependent on something that I can't word well right now.
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


    Integer[] getRow(Integer[][] board, int i){
        return board[i];
    }

    Integer[] getCol(Integer[][] board, int j){
        Integer[] col = new Integer[board.length];
        for (int k = 0; k < col.length; k++) {
            col[k] = board[k][j];
        }
        return col;
    }

    Integer[] getSubsection(Integer[][] board, int i, int j) {
        Integer[] subsection = new Integer[board.length];
        int subRow = (i / 3) * 3;
        int subCol = (j / 3) * 3;
        int m = 0;

        for (int k = subRow; k < subRow + 3; k++) {
            for (int l = subCol; l < subCol + 3; l++) {
                subsection[m] = board[k][l];
                m++;
            }
        }
        return subsection;
    }

    // Pretty self explanatory. It prints the board.
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

    boolean singleCandidate(ConcurrentSkipListMap<String, ConcurrentSkipListMap<Integer, Integer>> potentials){
        // deep copy this.board
        Integer[][] board = new Integer[this.board.length][this.board[0].length];
        for (int i = 0; i < this.board.length; i++) {
            if (this.board[i].length >= 0)
                System.arraycopy(this.board[i], 0, board[i], 0, this.board[i].length);
        }
        boolean techCheck;
        // check if potentials is empty
        // check second dimension in potentials
        while(!this.isEmpty(potentials)){
            // potentials treeMap iteration
//            for(String boardKeys : potentials.keySet())
//                for(Integer potKeys : potentials.get(boardKeys).keySet())
//                    System.out.println(boardKeys + "," + potKeys + ":" + potentials.get(boardKeys).get(potKeys));
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    String key = String.valueOf(i) + String.valueOf(j);
                    if (potentials.get(key).size() == 1) {
                        int num = (int) potentials.get(key).values().toArray()[0];
                        board[i][j] = num;
                        removePotentials(potentials, i, j, num);
                    }
                }
            }
        }
        techCheck = isFull(board);
        if(techCheck){
            System.out.println("Single Candidate: Passed");
            return true;
        }else{
            System.out.println("Single Candidate: Failed");
            return false;
        }
    }

    boolean singlePosition(ConcurrentSkipListMap<String, ConcurrentSkipListMap<Integer, Integer>> potentials){
        Integer[][] board = new Integer[this.board.length][this.board[0].length];
        for (int i = 0; i < this.board.length; i++) {
            if (this.board[i].length >= 0)
                System.arraycopy(this.board[i], 0, board[i], 0, this.board[i].length);
        }
        boolean techCheck;

        // some sort of new while statement needs to go here for multiple run-throughs (infinite loop if can't be solved).
        while(!this.isEmpty(potentials)){
//            for(String boardKeys : potentials.keySet())
//                for(Integer potKeys : potentials.get(boardKeys).keySet())
//                    System.out.println(boardKeys + "," + potKeys + ":" + potentials.get(boardKeys).get(potKeys));
            for(int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    String key = String.valueOf(i) + String.valueOf(j);
                    for (Integer keyIdx : potentials.get(key).keySet()) {
                        int num;
                        try{
                            num = potentials.get(key).get(keyIdx);
                        }catch(NullPointerException e){ break; }
                        potentials.get(key).clear();
                        if (!Arrays.asList(getRow(board, i)).contains(num) &&
                                !Arrays.asList(getCol(board, j)).contains(num) &&
                                !Arrays.asList(getCol(board, j)).contains(num)) {
                            board[i][j] = num;
                            potentials.get(key).clear();
                            removePotentials(potentials, i, j, num);
                        }
                    }
                }
            }
        }

        techCheck = isFull(board);
        if(techCheck){
            System.out.println("Single Position: Passed");
            return true;
        }else{
            System.out.println("Single Position: Failed");
            return false;
        }
    }

    boolean candidateLine(ConcurrentSkipListMap<String, ConcurrentSkipListMap<Integer, Integer>> potentials){
        boolean techCheck;
        Integer[][] board = new Integer[this.board.length][this.board[0].length];
        for (int i = 0; i < this.board.length; i++) {
            if (this.board[i].length >= 0)
                System.arraycopy(this.board[i], 0, board[i], 0, this.board[i].length);
        }

        // potentials removal technique

        // find "lines" (subsections that have a particular number only in a line)
        for(int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                String key = String.valueOf(i) + String.valueOf(j);
//                System.out.println("\ncurrent position:  " + key);
                for (Integer keyIdx : potentials.get(key).keySet()) {
                    int num = potentials.get(key).get(keyIdx);
                    int subI = (i / 3) * 3;
                    int subJ = (j / 3) * 3;
                    int numCount = 0;
                    Boolean lineCheck = false;
                    String line = "";
                    // check rows for lines
//                    System.out.println("\nRow lines");
//                    System.out.println("num: " + num);
                    // FIXME: There must be a way to improve the iteration here with both rows and columns. It's very repetitive rn.
                    rowLoop:
                    for(int subIIdx = subI; subIIdx < (subI + 3); subIIdx++) {
                        for (int subJIdx = subJ; subJIdx < (subJ + 3); subJIdx++) {
//                            System.out.println("sub pos: " + subIIdx + subJIdx);
                            if (potentials.get(String.valueOf(subIIdx) + String.valueOf(subJIdx)).containsValue(num)) {
                                numCount++;
//                                System.out.println("numCount: " + numCount);
                            }
                        }
                        if(numCount > 1 && !lineCheck){
//                            System.out.println("Possible row line at " + key);
                            lineCheck = true;
                            line = "row:" + subIIdx + ":" + subJ;
                            for (int subIIdxLine = subI; subIIdxLine < (subI + 3); subIIdxLine++) {
                                for (int subJIdxLine = subJ; subJIdxLine < (subJ + 3); subJIdxLine++) {
                                    if(subIIdxLine != subIIdx) {
                                        if (potentials.get(String.valueOf(subIIdxLine) + String.valueOf(subJIdxLine)).containsValue(num)) {
//                                            System.out.println("Invalid line.....aborted");
                                            lineCheck = false;
                                            line = "";
                                            break rowLoop;
                                        }
                                    }
                                }
                            }
                        }
                        numCount = 0;
                    }

                    // check column for lines
//                    System.out.println("\nColumn lines");
                    columnLoop:
                    for (int subJIdx = subJ; subJIdx < (subJ + 3); subJIdx++) {
                        for(int subIIdx = subI; subIIdx < (subI + 3); subIIdx++) {
//                            System.out.println("sub pos: " + subIIdx + subJIdx);
                            if (potentials.get(String.valueOf(subIIdx) + String.valueOf(subJIdx)).containsValue(num)) {
                                numCount++;
//                                System.out.println("numCount: " + numCount);
                            }
                        }
                        if(numCount > 1 && !lineCheck){
//                            System.out.println("Possible column line at " + key);
                            lineCheck = true;
                            line = "col:" + subJIdx + ":" + subI;
                            for (int subJIdxLine = subJ; subJIdxLine < (subJ + 3); subJIdxLine++) {
                                for (int subIIdxLine = subI; subIIdxLine < (subI + 3); subIIdxLine++) {
                                    if(subJIdxLine != subJIdx) {
                                        if (potentials.get(String.valueOf(subIIdxLine) + String.valueOf(subJIdxLine)).containsValue(num)) {
                                            lineCheck = false;
                                            line = "";
                                            break columnLoop;
                                        }
                                    }
                                }
                            }
                        }
                        numCount = 0;
                    }
                    // Remove values if line is valid
                    if(lineCheck){
//                        System.out.println("FOUND LINE: " + line);
                        String[] lineInfo = line.split(":");
                        if(lineInfo[0].equals("row")){
                            for(int subJIdx = 0; subJIdx < board[0].length; subJIdx++){
//                                System.out.println("Attempting removal at " + lineInfo[1] + subJIdx);
                                if(subJIdx < Integer.parseInt(lineInfo[2]) || subJIdx > (Integer.parseInt(lineInfo[2])+2)) {
//                                        System.out.println("Removed at " + lineInfo[1] + subJIdx);
                                    potentials.get(lineInfo[1] + String.valueOf(subJIdx)).values().remove(num);
                                }
                            }
                        }else if(lineInfo[0].equals("col")){
                            for(int subIIdx = 0; subIIdx < board[0].length; subIIdx++){
//                                System.out.println("Attempting removal at " + subIIdx + lineInfo[1]);
                                if(subIIdx < Integer.parseInt(lineInfo[2]) || subIIdx > (Integer.parseInt(lineInfo[2])+2)) {
//                                        System.out.println("Removed at " + subIIdx + lineInfo[1]);
                                    potentials.get(String.valueOf(subIIdx) + lineInfo[1]).values().remove(num);
                                }
                            }
                        }
                    }
                }
            }
        }

        techCheck = isFull(board);
        if(techCheck){
            System.out.println("Candidate Line: Passed");
            return true;
        }else{
            System.out.println("Candidate Line: Failed");
            return false;
        }
    }

    boolean doublePair(ConcurrentSkipListMap<String, ConcurrentSkipListMap<Integer, Integer>> potentials){
        boolean techCheck;
        Integer[][] board = new Integer[this.board.length][this.board[0].length];
        for (int i = 0; i < this.board.length; i++) {
            if (this.board[i].length >= 0)
                System.arraycopy(this.board[i], 0, board[i], 0, this.board[i].length);
        }

        // potentials removal technique

        // implementation of double pair technique
        // find pair in current subsection
        // find double pair in the proceeding subsection
        // remove potentials in other subsections without pair

        for(int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                String key = String.valueOf(i) + String.valueOf(j);
                for(Integer keyIdx : potentials.get(key).keySet()){
                    int subI = (i / 3) * 3;
                    int subJ = (j / 3) * 3;
                    int num = potentials.get(key).get(keyIdx);
                    // WIP
                }
            }
        }

        techCheck = isFull(board);
        if(techCheck){
            System.out.println("Double Pair: Passed");
            return true;
        }else{
            System.out.println("Double Pair: Failed");
            return false;
        }
    }

    boolean nakedPairs(){
        return true;
    }

    boolean hiddenPairs(){
        return true;
    }

    boolean lockedPairs(){
        return true;
    }


    ConcurrentSkipListMap<String, ConcurrentSkipListMap<Integer, Integer>> getPotentials(){
        ConcurrentSkipListMap<String, ConcurrentSkipListMap<Integer, Integer>> potentials = new ConcurrentSkipListMap<>();
        Integer[] row;
        Integer[] col;
        Integer[] subsection;
        for(int i = 0; i < this.board.length; i++){
            for(int j = 0; j < this.board[i].length; j++){
                // generate (3d) multidimensional array of potentials for each cell
                // find single candidate potentials and fill board accordingly
                // if not singles are left, board cannot be solved and return false
                String key = String.valueOf(i) + String.valueOf(j); // board index keys

                // generate potentials
                int[] validValues = {1,2,3,4,5,6,7,8,9};
                row = getRow(this.board, i);
                col = getCol(this.board, j);
                subsection = getSubsection(this.board, i, j);

                for(int k = 0; k < validValues.length; k++) {
                    if (Arrays.asList(row).contains(validValues[k]) || Arrays.asList(col).contains(validValues[k])
                            || Arrays.asList(subsection).contains(validValues[k])){
                        validValues[k] = 0;
                    }
                }
                int index = 0;
                potentials.put(key, new ConcurrentSkipListMap<>());
                for(int k = 0; k < validValues.length; k++){
                    if(validValues[k] != 0 && this.board[i][j].equals(0)){
                        potentials.get(key).put(index, validValues[k]);
                        index++;
                    }
                }
            }
        }
        return potentials;
    }

    void removePotentials(ConcurrentSkipListMap<String, ConcurrentSkipListMap<Integer, Integer>> potentials, int i, int j, int refNum){
        // remove this value from all other cells in row, col and sub
        // iterate all potentials and remove (now invalid) values
        for (int k = 0; k < getRow(board, i).length; k++) {
            int subI = (i / 3) * 3;
            int subJ = (j / 3) * 3;
            // get subI and subJ values in subsection from k and current i, j values
            int numIterations = 0;
            for (int idxI = subI; idxI < (subI + 3); idxI++) {
                for (int idxJ = subJ; idxJ < (subJ + 3); idxJ++) {
                    if (numIterations == k) {
                        subI = idxI;
                        subJ = idxJ;
                    }
                    numIterations++;
                }
            }
            // row, col, subsection potentials removal
            if (potentials.get(String.valueOf(i) + String.valueOf(k)).containsValue(refNum)) {
//                System.out.println("Remove row refNum: " + refNum);
                potentials.get(String.valueOf(i) + String.valueOf(k)).values().remove(refNum);
            } else if (potentials.get(String.valueOf(k) + String.valueOf(j)).containsValue(refNum)) {
//                System.out.println("Remove column refNum: " + refNum);
                potentials.get(String.valueOf(k) + String.valueOf(j)).values().remove(refNum);
            } else if (potentials.get(String.valueOf(subI) + String.valueOf(subJ)).containsValue(refNum)) {
//                System.out.println("Remove subsection refNum: " + refNum);
                potentials.get(String.valueOf(subI) + String.valueOf(subJ)).values().remove(refNum);
            }
        }
    }

    void setGUI(JTextField[][] tfCells){
        this.tfCells = tfCells;
    }

    boolean isEmpty(Integer[][] board){
        for(int i = 0; i < board.length; i++)
            for(int j = 0; j < board[i].length; j++)
                if(!board[i][j].equals(0))
                    return false;
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean isEmpty(ConcurrentSkipListMap<String, ConcurrentSkipListMap<Integer, Integer>> map){
        for(String initKeys : map.keySet())
            if (!map.get(initKeys).isEmpty())
                return false;
        return true;
    }

    boolean isFull(Integer[][] board){
        for(int i = 0; i < board.length; i++)
            for(int j = 0; j < board[i].length; j++)
                if(board[i][j].equals(0))
                    return false;
        return true;
    }

    boolean canSolve() {
        // run the most simple technique (single position) then try the second and if the board isn't full, \n
        // try the first again. If not, try the third, then second, first, and so on. You probably get the point ;)
//        if(!this.singlePosition(getPotentials())){
//            if(!this.singleCandidate(getPotentials())){
//                // TODO: Use multiple techniques on one try (candidate line requires more than one technique. It cannot solve it on its own)
//                // FIXME: Rethink this through. Try to use recursive methods instead??????
//                return this.candidateLine(getPotentials());
//            }else{
//                return true;
//            }
//        }else{
//            return true;
//        }

        try {
            this.singleCandidate(getPotentials());
        } catch (Exception e) {
            //TODO: handle exception
            System.err.println("Error in single candidate: " + e);
        }

        try {
            this.singlePosition(getPotentials());
        } catch (Exception e) {
            //TODO: handle exception
            System.err.println("Error in single position: " + e);
        }

        try {
            this.candidateLine(getPotentials());
        } catch (Exception e) {
            //TODO: handle exception
            System.err.println("Error in candidate line: " + e);
        }

        try {
            this.doublePair(getPotentials());
        } catch (Exception e) {
            //TODO: handle exception
            System.err.println("Error in double pair: " + e);
        }
        
        return true;
    }
}