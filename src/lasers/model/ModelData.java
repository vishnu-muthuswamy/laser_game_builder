package lasers.model;

/**
 * Use this class to customize the data you wish to send from the model
 * to the view when the model changes state.
 *
 * @author RIT CS
 * @author Lukowski, Matthew and Muthuswamy, Vishnu
 */
public class ModelData {

    /**
     * Operation status message enum
     */
     public enum Status {
        ERROR_ADDING,
        ERROR_REMOVING,
        ERROR_VERIFYING,
        ERROR_FNF,
        NO_ERROR_ADDING,
        NO_ERROR_REMOVING,
        NO_ERROR_VERIFYING,
        SOLUTION,
        NO_SOLUTION,
        RESTART
    }

    private Status status;
    private String statusMessage;

    private String safeFilename;

    private int row;
    private int col;

    /**
     * The constructor for ModelData, which initializes all its variables.
     */
    public ModelData (Status status, String safeFilename, int row, int col) {
        this.status = status;
        this.safeFilename = safeFilename;
        this.row = row;
        this.col = col;

        this.createStatusMessage();
    }

    /**
     * Accessor for status
     */
    public Status getStatus() {return status;}

    /**
     * Accessor for status message
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Accessor for row of tile
     */
    public int getRow() {return row;}

    /**
     * Accessor for col of tile
     */
    public int getCol() {return col;}

    /**
     * This message creates a status message based on teh given information.
     */
    private void createStatusMessage () {
        if (status == Status.ERROR_ADDING) {
            statusMessage = "Error adding laser at: (" + row + ", " + col + ")";
        } else if (status == Status.ERROR_REMOVING) {
            statusMessage = "Error removing laser at: (" + row + ", " + col + ")";
        } else if (status == Status.ERROR_VERIFYING) {
            statusMessage = "Error verifying at: (" + row + ", " + col + ")";
        } else if (status == Status.ERROR_FNF) {
            statusMessage = "File cannot be found";
        } else if (status == Status.NO_ERROR_ADDING) {
            statusMessage = "Laser added at: (" + row + ", " + col + ")";
        } else if (status == Status.NO_ERROR_REMOVING) {
            statusMessage = "Removed laser at: (" + row + ", " + col + ")";
        } else if (status == Status.NO_ERROR_VERIFYING) {
            statusMessage = "Safe is fully verified!";
        } else if (status == Status.SOLUTION) {
            statusMessage = safeFilename + " solved!";
        } else if (status == Status.NO_SOLUTION) {
            statusMessage = safeFilename + " has no solution!";
        }else if (status == Status.RESTART) {
            statusMessage = safeFilename + " has been reset:";
        }
    }
}
