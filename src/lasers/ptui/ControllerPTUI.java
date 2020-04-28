package lasers.ptui;

import lasers.model.LasersModel;

import java.io.*;

/**
 * This class represents the controller portion of the plain text UI.
 * It takes the model from the view (LasersPTUI) so that it can perform
 * the operations that are input in the run method.
 *
 * @author RIT CS
 * @author Lukowski, Matthew and Muthuswamy, Vishnu
 */
public class ControllerPTUI  {
    /** The UI's connection to the lasers.lasers.model */
    private LasersModel model;

    /**
     * Construct the PTUI.  Create the model and initialize the view.
     * @param model The laser model
     */
    public ControllerPTUI(LasersModel model) {
        this.model = model;
        this.model.makeBoard();
    }

    private void mainLoopFileIn(BufferedReader file) {

        try {

            int row;
            int col;

            String[] input;
            String fileIn = file.readLine();

            while (fileIn != null) {
                System.out.print("> ");
                System.out.println(fileIn);

                if (fileIn.length() == 0 || fileIn.split(" ").length == 0) {
                    continue;
                } else if (fileIn.charAt(0) == 'q') {
                    break;
                } else if (fileIn.charAt(0) == 'a' || fileIn.charAt(0) == 'r') {
                    input = fileIn.split(" ");
                    if (input.length == 3) {
                        row = Integer.parseInt(input[1]);
                        col = Integer.parseInt(input[2]);
                        if (fileIn.charAt(0) == 'a') {
                            model.add(row, col);
                        } else {
                            model.remove(row, col);
                        }
                    } else {
                        System.out.println("Incorrect coordinates");
                    }
                } else if (fileIn.charAt(0) == 'd') {
                    System.out.print(model);
                } else if (fileIn.charAt(0) == 'h') {
                    model.help();
                } else if (fileIn.charAt(0) == 'v') {
                    model.verify();
                } else {
                    System.out.println("Unrecognized command: " + fileIn);
                }
                fileIn = file.readLine();
            }
        } catch (IOException ioe) {
            System.out.println("File cannot be found");
        }
    }

    /**
     * This is the input handling loop for standard input.
     */
    private void mainLoopStdIn() {

        try {
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));

            int row;
            int col;

            String[] input;
            String stdIn;

            while (true) {
                System.out.print("> ");
                stdIn = userIn.readLine();

                if (stdIn.length() == 0 || stdIn.split(" ").length == 0) {
                    continue;
                } else if (stdIn.charAt(0) == 'q') {
                    break;
                } else if (stdIn.charAt(0) == 'a' || stdIn.charAt(0) == 'r') {
                    input = stdIn.split(" ");
                    if (input.length == 3) {
                        row = Integer.parseInt(input[1]);
                        col = Integer.parseInt(input[2]);
                        if (stdIn.charAt(0) == 'a') {
                            model.add(row, col);
                        }
                        else {
                            model.remove(row, col);
                        }
                    } else {
                        System.out.println("Incorrect coordinates");
                    }
                } else if (stdIn.charAt(0) == 'd') {
                    System.out.print(model);
                } else if (stdIn.charAt(0) == 'h') {
                    model.help();
                } else if (stdIn.charAt(0) == 'v') {
                    model.verify();
                } else {
                    System.out.println("Unrecognized command: " + stdIn);
                }
            }
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }

    /**
     * Run the main loop.  This is the entry point for the controller
     * @param inputFile The name of the input command file, if specified
     */
    public void run(String inputFile) {

        if (inputFile == null) {
            mainLoopStdIn();
        }
        else {
            File commandFile = new File(inputFile);

            try (BufferedReader readCommandFile = new BufferedReader(new FileReader(commandFile))) {

                mainLoopFileIn(readCommandFile);
                mainLoopStdIn();

            } catch (IOException ioe) {
                System.out.println("File cannot be found");
            }
        }

    }
}
