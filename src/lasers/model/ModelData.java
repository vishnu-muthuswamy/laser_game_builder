package lasers.model;

/**
 * Use this class to customize the data you wish to send from the model
 * to the view when the model changes state.
 *
 * @author RIT CS
 * @author YOUR NAME HERE
 */
public class ModelData {
    // put status messages here
     public enum Status {
        ERROR_ADDING,
        ERROR_REMOVING,
        ERROR_VERIFYING,
        ERROR_FNF,
        NO_ERROR_ADDING,
        NO_ERROR_REMOVING,
        NO_ERROR_VERIFYING,
    }

    private Status status;
    private String statusMessage;

    private int row;
    private int col;

    public ModelData (Status status, int row, int col) {
        this.status = status;
        this.row = row;
        this.col = col;

        this.createStatusMessage();
    }

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
        }
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
