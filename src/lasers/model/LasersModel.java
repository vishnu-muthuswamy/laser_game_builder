package lasers.model;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * The model of the lasers safe.  You are free to change this class however
 * you wish, but it should still follow the MVC architecture.
 *
 * @author RIT CS
 * @author Lukowski, Matthew and Muthuswamy, Vishnu
 */
public class LasersModel {
    /** the observers who are registered with this model */
    private List<Observer<LasersModel, ModelData>> observers;

    private File safeFile;
    private BufferedReader readSafeFile;

    private int row;
    private int col;

    private String[][] board;

    public LasersModel(String filename) throws FileNotFoundException {
        this.observers = new LinkedList<>();

        this.safeFile = new File(filename);
        this.readSafeFile = new BufferedReader(new FileReader(safeFile));

        this.row = 0;
        this.col = 0;
    }

    /**
     * Add a new observer.
     *
     * @param observer the new observer
     */
    public void addObserver(Observer<LasersModel, ModelData > observer) {
        this.observers.add(observer);
    }

    /**
     * Notify observers the model has changed.
     *
     * @param data optional data the model can send to the view
     */
    private void notifyObservers(ModelData data){
        for (Observer<LasersModel, ModelData> observer: observers) {
            observer.update(this, data);
        }
    }

    /**
     * This function reads from the command line and gets the dimensions
     * for the game board and also creates the game board based on
     * the input file.
     * @throws IOException
     */
    public void makeBoard() {
        try {
            String[] line = this.readSafeFile.readLine().split(" ");
            this.row = Integer.parseInt(line[0]);
            this.col = Integer.parseInt(line[1]);
            this.board = new String[this.row][this.col];
            for (int r = 0; r < this.row; r++) {
                line = this.readSafeFile.readLine().split(" ");
                for (int c = 0; c < this.col; c++) {
                    this.board[r][c] = line[c];
                }
            }
            readSafeFile.close();
        } catch (IOException ioe) {
            notifyObservers(new ModelData(ModelData.Status.ERROR_FNF, 0, 0));
        }
    }

    /**
     * This function prints out the board so the player
     * can see it.
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(" ");
        for (int c = 0; c < this.col; c++) {
            str.append(" " + (c % 10));
        }
        str.append("\n");
        str.append("  ");
        for (int c = 0; c < this.col * 2 - 1; c++) {
            str.append("-");
        }
        str.append("\n");
        for (int r = 0; r < this.row; r++) {
            str.append((r % 10) + "|");
            for (int c = 0; c < this.col; c++) {
                str.append(this.board[r][c] + " ");
            }
            str.append("\n");
        }

        return str.toString();
    }

    /**
     * This function adds a laser to the board and shots a laser
     * beam of  in 4 direction (North, South, East, and West)
     * @param rowCoordinate - the row coordinate of the board
     * @param colCoordinate - the column coordinate of the board
     */
    public void add(int rowCoordinate, int colCoordinate) {
        if (!this.board[rowCoordinate][colCoordinate].equals(".") &&
                !this.board[rowCoordinate][colCoordinate].equals("*")) {
            notifyObservers(new ModelData(ModelData.Status.ERROR_ADDING, rowCoordinate, colCoordinate));
            return;
        }

        this.board[rowCoordinate][colCoordinate] = "L";

        for (int r = rowCoordinate + 1; r < this.row; r++) {
            if (!this.board[r][colCoordinate].equals(".") &&
                    !this.board[r][colCoordinate].equals("*")) {
                break;
            }
            this.board[r][colCoordinate] = "*";
        }
        for (int r = rowCoordinate - 1; r >= 0; r--) {
            if (!this.board[r][colCoordinate].equals(".") &&
                    !this.board[r][colCoordinate].equals("*")) {
                break;
            }
            this.board[r][colCoordinate] = "*";
        }
        for (int c = colCoordinate + 1; c < this.col; c++) {
            if (!this.board[rowCoordinate][c].equals(".") &&
                    !this.board[rowCoordinate][c].equals("*")) {
                break;
            }
            this.board[rowCoordinate][c] = "*";
        }
        for (int c = colCoordinate - 1; c >= 0; c--) {
            if (!this.board[rowCoordinate][c].equals(".") &&
                    !this.board[rowCoordinate][c].equals("*")) {
                break;
            }
            this.board[rowCoordinate][c] = "*";
        }

        notifyObservers(new ModelData(ModelData.Status.NO_ERROR_ADDING, rowCoordinate, colCoordinate));
    }

    /**
     * This function removes a laser along with all the laser beams
     * that the laser emitted.
     * @param rowCoordinate - the row coordinate of the board
     * @param colCoordinate - the column coordinate of the board
     */
    public void remove(int rowCoordinate, int colCoordinate){
        if(!this.board[rowCoordinate][colCoordinate].equals("L")) {
            notifyObservers(new ModelData(ModelData.Status.ERROR_REMOVING, rowCoordinate, colCoordinate));
            return;
        }

        this.board[rowCoordinate][colCoordinate] = ".";

        removeNorthSouthLaserBeams(rowCoordinate, colCoordinate);
        removeWestEastLaserBeams(rowCoordinate, colCoordinate);

        notifyObservers(new ModelData(ModelData.Status.NO_ERROR_REMOVING, rowCoordinate, colCoordinate));
    }

    /**
     * A helper function for remove() that removes northern and southern laser beams.
     * @param rowCoordinate - the row coordinate of the board
     * @param colCoordinate - the column coordinate of the board
     */
    private void removeNorthSouthLaserBeams(int rowCoordinate, int colCoordinate) {
        boolean laserInLine = false;
        int r = rowCoordinate-1;

        //North lasers
        while (r >= 0) {
            if (!this.board[r][colCoordinate].equals("*") && !this.board[r][colCoordinate].equals("L")) {
                break;
            }
            for (int up = rowCoordinate - 1; up >= 0; up--) {
                if (this.board[up][colCoordinate].equals("L")) {
                    for (int row = up + 1; row < this.row; row++) {
                        if (!this.board[row][colCoordinate].equals("*") &&
                                !this.board[row][colCoordinate].equals(".")) {
                            return;
                        }
                        this.board[row][colCoordinate] = "*";
                    }
                    laserInLine = true;
                    break;
                }
            }
            if (laserInLine) {
                break;
            }

            verifyLaserIntersectionsNorthSouthLaserBeams(r, colCoordinate);

            r--;
        }

        laserInLine = false;
        r = rowCoordinate+1;

        //South lasers
        while (r < this.row) {
            if(!this.board[r][colCoordinate].equals("*") && !this.board[r][colCoordinate].equals("L")){
                break;
            }
            for (int down = rowCoordinate + 1; down < this.row; down++) {
                if (this.board[down][colCoordinate].equals("L")) {
                    for (int row = down - 1; row >= 0; row--) {
                        if (!this.board[row][colCoordinate].equals("*") &&
                                !this.board[row][colCoordinate].equals(".")) {
                            return;
                        }
                        this.board[row][colCoordinate] = "*";
                    }
                    laserInLine = true;
                    break;
                }
            }
            if(laserInLine){
                break;
            }

            verifyLaserIntersectionsNorthSouthLaserBeams(r, colCoordinate);

            r++;
        }
    }

    private void verifyLaserIntersectionsNorthSouthLaserBeams(int r, int colCoordinate) {
        for (int col = colCoordinate; col < this.col; col++) {
            if (this.board[r][col].equals("*") && col == this.col - 1) {
                this.board[r][colCoordinate] = ".";
            } else if (this.board[r][col].equals("L")) {
                this.board[r][colCoordinate] = "*";
                break;
            } else if (!this.board[r][col].equals("*") && !this.board[r][col].equals("L")) {
                this.board[r][colCoordinate] = ".";
                break;
            }
        }

        if (!this.board[r][colCoordinate].equals("*")) {
            for (int col = colCoordinate - 1; col >= 0; col--) {
                if (this.board[r][col].equals("*") && col == 0) {
                    this.board[r][colCoordinate] = ".";
                } else if (this.board[r][col].equals("L")) {
                    this.board[r][colCoordinate] = "*";
                    break;
                } else if (!this.board[r][col].equals("*") && !this.board[r][col].equals("L")) {
                    this.board[r][colCoordinate] = ".";
                    break;
                }
            }
        }
    }

    /**
     * A helper function for remove() that removes western and eastern laser beams.
     * @param rowCoordinate - the row coordinate of the board
     * @param colCoordinate - the column coordinate of the board
     */
    private void removeWestEastLaserBeams(int rowCoordinate, int colCoordinate) {
        boolean laserInLine = false;
        int c = colCoordinate-1;

        //West lasers
        while (c >= 0) {
            if(!this.board[rowCoordinate][c].equals("*") && !this.board[rowCoordinate][c].equals("L")){
                break;
            }
            for (int left = colCoordinate-1; left >= 0; left--) {
                if (this.board[rowCoordinate][left].equals("L")) {
                    for (int col = left + 1; col < this.col; col++) {
                        if (!this.board[rowCoordinate][col].equals("*") &&
                                !this.board[rowCoordinate][col].equals(".")) {
                            return;
                        }
                        this.board[rowCoordinate][col] = "*";
                    }
                    laserInLine = true;
                    break;
                }
            }
            if(laserInLine){
                break;
            }

            verifyLaserIntersectionsWestEastLaserBeams(rowCoordinate, c);

            c--;
        }

        laserInLine = false;
        c = colCoordinate+1;

        //East lasers
        while (c < this.col) {
            if(!this.board[rowCoordinate][c].equals("*") && !this.board[rowCoordinate][c].equals("L")){
                break;
            }
            for (int right = colCoordinate+1; right < this.row; right++) {
                if (this.board[rowCoordinate][right].equals("L")) {
                    for (int col = right - 1; col >= 0; col--) {
                        if (!this.board[rowCoordinate][col].equals("*") &&
                                !this.board[rowCoordinate][col].equals(".")) {
                            return;
                        }
                        this.board[rowCoordinate][col] = "*";
                    }
                    laserInLine = true;
                    break;
                }
            }
            if(laserInLine){
                break;
            }

            verifyLaserIntersectionsWestEastLaserBeams(rowCoordinate, c);

            c++;
        }
    }

    private void verifyLaserIntersectionsWestEastLaserBeams(int rowCoordinate, int c) {
        for (int row = rowCoordinate; row < this.row; row++) {
            if (this.board[row][c].equals("*") && row == this.row - 1) {
                this.board[rowCoordinate][c] = ".";
            } else if (this.board[row][c].equals("L")) {
                this.board[rowCoordinate][c] = "*";
                break;
            } else if (!this.board[row][c].equals("*") && !this.board[row][c].equals("L")) {
                this.board[rowCoordinate][c] = ".";
                break;
            }
        }

        if (!this.board[rowCoordinate][c].equals("*")) {
            for (int row = rowCoordinate - 1; row >= 0; row--) {
                if (this.board[row][c].equals("*") && row == 0) {
                    this.board[rowCoordinate][c] = ".";
                } else if (this.board[row][c].equals("L")) {
                    this.board[rowCoordinate][c] = "*";
                    break;
                } else if (!this.board[row][c].equals("*") && !this.board[row][c].equals("L")) {
                    this.board[rowCoordinate][c] = ".";
                    break;
                }
            }
        }
    }

    /**
     * This function displays the commands that can be used to play this game.
     */
    public void help() {
        System.out.println("a|add r c: Add laser to (r,c)");
        System.out.println("d|display: Display safe");
        System.out.println("h|help: Print this help message");
        System.out.println("q|quit: Exit program");
        System.out.println("r|remove r c: Remove laser from (r,c)");
        System.out.println("v|verify: Verify safe correctness");
    }

    /**
     * This function verifies the safe and returns an error if present.
     */
    public void verify() {
        int numOfAdjLasers;

        for (int r = 0; r < this.row; r++) {
            for (int c = 0; c < this.col; c++) {
                if (this.board[r][c].equals(".")) {
                    notifyObservers(new ModelData(ModelData.Status.ERROR_VERIFYING, r, c));
                    return;
                } else if (this.board[r][c].equals("0")) {

                    numOfAdjLasers = findAdjLasers(r, c);

                    if (numOfAdjLasers != 0) {
                        notifyObservers(new ModelData(ModelData.Status.ERROR_VERIFYING, r, c));
                        return;
                    }
                } else if (this.board[r][c].equals("1")) {

                    numOfAdjLasers = findAdjLasers(r, c);

                    if (numOfAdjLasers != 1) {
                        notifyObservers(new ModelData(ModelData.Status.ERROR_VERIFYING, r, c));
                        return;
                    }
                } else if (this.board[r][c].equals("2")) {

                    numOfAdjLasers = findAdjLasers(r, c);

                    if (numOfAdjLasers != 2) {
                        notifyObservers(new ModelData(ModelData.Status.ERROR_VERIFYING, r, c));
                        return;
                    }
                } else if (this.board[r][c].equals("3")) {

                    numOfAdjLasers = findAdjLasers(r, c);

                    if (numOfAdjLasers != 3) {
                        notifyObservers(new ModelData(ModelData.Status.ERROR_VERIFYING, r, c));
                        return;
                    }
                } else if (this.board[r][c].equals("4")) {

                    numOfAdjLasers = findAdjLasers(r, c);

                    if (numOfAdjLasers != 4) {
                        notifyObservers(new ModelData(ModelData.Status.ERROR_VERIFYING, r, c));
                        return;
                    }
                } else if (this.board[r][c].equals("L")) {

                    for (int rowCoordinate = r + 1; rowCoordinate < this.row; rowCoordinate++) {
                        if (!this.board[rowCoordinate][c].equals("L") &&
                                !this.board[rowCoordinate][c].equals("*")) {
                            break;
                        } else if (this.board[rowCoordinate][c].equals("L")) {
                            notifyObservers(new ModelData(ModelData.Status.ERROR_VERIFYING, r, c));
                            return;
                        }
                    }
                    for (int rowCoordinate = r - 1; rowCoordinate >= 0; rowCoordinate--) {
                        if (!this.board[rowCoordinate][c].equals("L") &&
                                !this.board[rowCoordinate][c].equals("*")) {
                            break;
                        } else if (this.board[rowCoordinate][c].equals("L")) {
                            notifyObservers(new ModelData(ModelData.Status.ERROR_VERIFYING, r, c));
                            return;
                        }
                    }
                    for (int colCoordinate = c + 1; colCoordinate < this.col; colCoordinate++) {
                        if (!this.board[r][colCoordinate].equals("L") &&
                                !this.board[r][colCoordinate].equals("*")) {
                            break;
                        } else if (this.board[r][colCoordinate].equals("L")) {
                            notifyObservers(new ModelData(ModelData.Status.ERROR_VERIFYING, r, c));
                            return;
                        }
                    }
                    for (int colCoordinate = c - 1; colCoordinate >= 0; colCoordinate--) {
                        if (!this.board[r][colCoordinate].equals("L") &&
                                !this.board[r][colCoordinate].equals("*")) {
                            break;
                        } else if (this.board[r][colCoordinate].equals("L")) {
                            notifyObservers(new ModelData(ModelData.Status.ERROR_VERIFYING, r, c));
                            return;
                        }
                    }
                }
            }
        }

        notifyObservers(new ModelData(ModelData.Status.NO_ERROR_VERIFYING, 0, 0));
    }

    /**
     * A helper function for verify() that finds the number of adjacent lasers for each pillar.
     * @param row - the row coordinate of the pillar
     * @param col - the column coordinate of the pillar
     */
    private int findAdjLasers(int row, int col) {
        int numOfAdjLasers = 0;
        int rowMaxIndex = this.row - 1;
        int colMaxIndex = this.col - 1;

        List<String> cardinalDirections = Arrays.asList("N", "S", "W", "E");

        for(String cardinalDirection: cardinalDirections) {
            switch (cardinalDirection) {
                case "N":
                    if (row != 0) {
                        if (this.board[row - 1][col].equals("L")) {
                            numOfAdjLasers++;
                        }
                    }
                    break;
                case "S":
                    if (row != rowMaxIndex) {
                        if (this.board[row + 1][col].equals("L")) {
                            numOfAdjLasers++;
                        }
                    }
                    break;
                case "W":
                    if (col != 0) {
                        if (this.board[row][col - 1].equals("L")) {
                            numOfAdjLasers++;
                        }
                    }
                    break;
                case "E":
                    if (col != colMaxIndex) {
                        if (this.board[row][col + 1].equals("L")) {
                            numOfAdjLasers++;
                        }
                    }
                    break;
            }
        }

        return numOfAdjLasers;
    }

    /**
     * This fucntion returns the game board
     * @return - 2D string array that is a
     * game board
     */
    public String[][] getBoard(){
        return this.board;
    }

    /**
     * This fuction gets the length of the
     * row of the board
     * @return - the row length
     */
    public int getRowDim(){
        return this.row;
    }

    /**
     * This function gets the length of the
     * col of the board
     * @return - the col length
     */
    public int getColDim(){
        return this.col;
    }
}
