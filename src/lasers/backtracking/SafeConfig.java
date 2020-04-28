package lasers.backtracking;

import lasers.model.LasersModel;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * The class represents a single configuration of a safe.  It is
 * used by the backtracker to generate successors, check for
 * validity, and eventually find the goal.
 *
 * This class is given to you here, but it will undoubtedly need to
 * communicate with the model.  You are free to move it into the lasers.model
 * package and/or incorporate it into another class.
 *
 * @author RIT CS
 * @author Lukowski, Matthew and Muthuswamy, Vishnu
 */
public class SafeConfig implements Configuration {

    private LasersModel model;
    private int row;
    private int rowDim;
    private int col;
    private int colDim;
    private String[][] board;

    /**
     * Constructor for the safe config
     * @param filename - name of the file
     * @throws FileNotFoundException
     */
    public SafeConfig(String filename) throws FileNotFoundException {
        this.model = new LasersModel(filename);
        this.model.makeBoard();
        this.board = this.model.getBoard();
        this.rowDim = this.model.getRowDim();
        this.colDim = this.model.getColDim();
        this.row = 0;
        this.col = -1;

    }

    /**
     * The copied constructor used by getSuccessors()
     * @param other - the other SafeConfig
     * @param row - the row coord
     * @param col - the col coord
     */
    public SafeConfig(SafeConfig other, int row, int col) {
        this.colDim = other.colDim;
        this.rowDim = other.rowDim;
        this.row = other.row;
        this.col = other.col;
        this.board = new String[this.rowDim][this.colDim];
        for(int r = 0; r < this.rowDim; r++){
            System.arraycopy(other.board[r], 0, this.board[r], 0, this.rowDim);
        }
        if(this.board[this.row][this.col].equals("*") || this.board[this.row][this.col].equals(".")){
            this.board[this.row][this.col] = "L";
        }
    }

    /**
     * This function gets the successors for each row and coloum
     * amking a new SafeConfig for each space on the board
     * @return
     */
    @Override
    public Collection<Configuration> getSuccessors() {
        List<Configuration> successors = new LinkedList<>();
        for(int row = 0; row < this.rowDim; row++){
            SafeConfig child = new SafeConfig(this, row, this.col+1);
            successors.add(child);
        }
        return successors;
    }

    /**
     * Checks if the placment of the laser is valid
     * @return - true or false
     */
    @Override
    public boolean isValid() {
        //Searching down
        if(this.row+1 < this.rowDim) {
            for (int row = this.row + 1; row < this.rowDim; row++) {
                if (this.board[row][this.col].equals("L")) {
                    return false;
                }
                if (!this.board[row][this.col].equals("L") || !this.board[row][this.col].equals("*")
                        || this.board[row][this.col].equals(".")) {
                    break;
                }
            }
        }
        // Searching up
        if(this.row-1 > 0) {
            for (int row = this.row - 1; row >= 0; row--) {
                if (this.board[row][this.col].equals("L")) {
                    return false;
                }
                if (!this.board[row][this.col].equals("L") || !this.board[row][this.col].equals("*")
                        || this.board[row][this.col].equals(".")) {
                    break;
                }
            }
        }
        //Searching left
        if(this.col-1 > 0){
            for (int col = this.col - 1; col >= 0; col--) {
                if (this.board[this.row][col].equals("L")) {
                    return false;
                }
                if (!this.board[this.row][col].equals("L") || !this.board[this.row][col].equals("*")
                        || this.board[this.row][col].equals(".")) {
                    break;
                }
            }
        }
        //Searching right
        if(this.col + 1 < this.colDim){
            for (int col = this.col + 1; col < this.colDim; col++) {
                if (this.board[this.row][col].equals("L")) {
                    return false;
                }
                if (!this.board[this.row][col].equals("L") || !this.board[this.row][col].equals("*")
                        || this.board[this.row][col].equals(".")) {
                    break;
                }
            }
        }
        return true;
    }

    /**
     * This function checks if a config is the goal config
     * once it reaches the goal it checks all the pillars to see
     * if they have the correct number of laser next to them
     * @return
     */
    @Override
    public boolean isGoal() {
        if(this.col == this.colDim - 1){
            for(int r = 0; r < this.rowDim; r++){
                for(int c = 0; c < this.colDim; c++){
                    if(!this.board[r][c].equals("L") || !this.board[r][c].equals(".") || !this.board[r][c].equals("*")){
                        if(!pillarChecker(r,c)){
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * This function checks if the pillar has the correct number
     * of lasers around them.
     * @param row - the row coord
     * @param col - the col coord
     * @return - true or false
     */
    public boolean pillarChecker(int row, int col){
        int pillarCount = 0;
        if(this.board[row][col].equals("X")){
            return true;
        }
        int pillarNum = Integer.parseInt(this.board[row][col]);
        if(row == 0) {
            if(col == 0){
                // check below and right
                if(this.board[row+1][col].equals("L")){
                    pillarCount++;
                }
                if(this.board[row][col+1].equals("L")){
                    pillarCount++;
                }
            }
            else if(col == this.colDim -1){
                // check below and left
                if(this.board[row+1][col].equals("L")){
                    pillarCount++;
                }
                if(this.board[row][col-1].equals("L")){
                    pillarCount++;
                }
            }
            else{
                if(this.board[row+1][col].equals("L")){
                    pillarCount++;
                }
                if(this.board[row][col-1].equals("L")){
                    pillarCount++;
                }
                if(this.board[row][col-1].equals("L")){
                    pillarCount++;
                }
            }
        }
        else if(row == this.rowDim-1){
            if(col == 0){
                //check above and right
                if(this.board[row-1][col].equals("L")){
                    pillarCount++;
                }
                if(this.board[row][col+1].equals("L")){
                    pillarCount++;
                }
            }
            else if(col == this.colDim -1 ){
                //check above and left
                if(this.board[row-1][col].equals("L")){
                    pillarCount++;
                }
                if(this.board[row][col-1].equals("L")){
                    pillarCount++;
                }
            }
            else{
                if(this.board[row-1][col].equals("L")){
                    pillarCount++;
                }
                if(this.board[row][col+1].equals("L")){
                    pillarCount++;
                }
                if(this.board[row][col-1].equals("L")){
                    pillarCount++;
                }

            }
        }
        else if(col == 0){
            if(this.board[row-1][col].equals("L")){
                pillarCount++;
            }
            if(this.board[row+1][col].equals("L")){
                pillarCount++;
            }
            if(this.board[row][col+1].equals("L")){
                pillarCount++;
            }
        }
        else if(col == this.colDim-1){
            if(this.board[row-1][col].equals("L")){
                pillarCount++;
            }
            if(this.board[row+1][col].equals("L")){
                pillarCount++;
            }
            if(this.board[row][col-1].equals("L")){
                pillarCount++;
            }
        }
        else{
            //check all 4 dirs
            if(this.board[row-1][col].equals("L")){
                pillarCount++;
            }
            if(this.board[row+1][col].equals("L")){
                pillarCount++;
            }
            if(this.board[row][col-1].equals("L")){
                pillarCount++;
            }
            if(this.board[row][col+1].equals("L")){
                pillarCount++;
            }
        }
        return pillarCount == pillarNum;
    }

}
