package asg.ui;

import asg.BotProvider;
import asg.Vars;
import asg.bot.BotBase;
import asg.struct.Board;
import asg.struct.PlayerMarks;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.util.Duration;

import java.io.IOException;

/**
 * The OutputFrameController class.  It controls button input from the users when
 * playing the game.
 *
 * @author Jedid Ahn
 *
 */
public class OutputFrameController {
    @FXML
    private GridPane gameBoardPane;

    @FXML
    private GridPane scoreBoardPane;

    @FXML
    private Label roundsLeftLabel;
    @FXML
    private Label playerXName;
    @FXML
    private Label playerOName;
    @FXML
    private HBox playerXBoxPane;
    @FXML
    private HBox playerOBoxPane;
    @FXML
    private Label playerXScoreLabel;
    @FXML
    private Label playerOScoreLabel;
    private final Button[][] buttons = new Button[Vars.BOARD_ROW_COUNT][Vars.BOARD_COL_COUNT];


    private Board currentBoard;
    private BotBase botX, botO;


    /**
     * Set the name of player X (player) to be name1, set the name of player O (bot) to be name2,
     * and the number of rounds played to be rounds. This input is received from
     * the input frame and is output in the score board of the output frame.
     *
     * @param name1       Name of Player 1 (Player).
     * @param name2       Name of Player 2 (Bot).
     * @param rounds      The number of rounds chosen to be played.
     * @param firstPlayer X if Player 1 (X) is first, O if Player 2 (O) is first.
     */
    void getInput(String name1, String name2, Integer rounds, String player1Type, String player2Type, PlayerMarks firstPlayer) {
        this.playerXName.setText(name1);
        this.playerOName.setText(name2);
        this.roundsLeftLabel.setText(rounds.toString());

        this.currentBoard = new Board(firstPlayer, 2 * rounds);
        this.currentBoard.setAt(Vars.BOARD_ROW_COUNT - 2, 0, PlayerMarks.X);
        this.currentBoard.setAt(Vars.BOARD_ROW_COUNT - 1, 0, PlayerMarks.X);
        this.currentBoard.setAt(Vars.BOARD_ROW_COUNT - 2, 1, PlayerMarks.X);
        this.currentBoard.setAt(Vars.BOARD_ROW_COUNT - 1, 1, PlayerMarks.X);
        this.currentBoard.setAt(0, Vars.BOARD_COL_COUNT - 2, PlayerMarks.O);
        this.currentBoard.setAt(0, Vars.BOARD_COL_COUNT - 1, PlayerMarks.O);
        this.currentBoard.setAt(1, Vars.BOARD_COL_COUNT - 2, PlayerMarks.O);
        this.currentBoard.setAt(1, Vars.BOARD_COL_COUNT - 1, PlayerMarks.O);
        this.renderState();

        // Get bots
        try {
            this.botX = BotProvider.getBot(player1Type);
            this.botO = BotProvider.getBot(player2Type);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, String.format("Failed to instantiate bot: %n%s%n%n Exiting.", e.getClass().getSimpleName())).showAndWait();
            System.exit(1);
        }

        this.callMoveNextPlayer();
    }


    /**
     * Construct the 8x8 game board by creating a total of 64 buttons in a 2
     * dimensional array, and construct the 8x2 score board for scorekeeping
     */
    @FXML
    private void initialize() {
        // Construct game board with 8 rows.
        for (int i = 0; i < Vars.BOARD_ROW_COUNT; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / Vars.BOARD_ROW_COUNT);
            this.gameBoardPane.getRowConstraints().add(rowConst);
        }

        // Construct game board with 8 columns.
        for (int i = 0; i < Vars.BOARD_COL_COUNT; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / Vars.BOARD_COL_COUNT);
            this.gameBoardPane.getColumnConstraints().add(colConst);
        }

        // Style buttons and construct 8x8 game board.
        for (int i = 0; i < Vars.BOARD_ROW_COUNT; i++) {
            for (int j = 0; j < Vars.BOARD_COL_COUNT; j++) {
                this.buttons[i][j] = new Button();
                this.buttons[i][j].setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                this.buttons[i][j].setCursor(Cursor.HAND);
                this.gameBoardPane.add(this.buttons[i][j], j, i);

                // Add ActionListener to each button such that when it is clicked, it calls
                // the selected coordinates method with its i and j coordinates.
                final int finalI = i;
                final int finalJ = j;
                this.buttons[i][j].setOnAction(event -> {
                    try {
                        this.selectedCoordinates(finalI, finalJ);
                    } catch (IllegalStateException e) {
                        new Alert(Alert.AlertType.ERROR, "Invalid coordinates: Try again!").showAndWait();
                    }
                });
            }
        }

        // Construct score board with 8 rows.
        for (int i = 0; i < Vars.BOARD_ROW_COUNT; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / Vars.BOARD_ROW_COUNT);
            this.scoreBoardPane.getRowConstraints().add(rowConst);
        }

        // Construct score board with 2 column.
        for (int i = 0; i < 2; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / 2);
            this.scoreBoardPane.getColumnConstraints().add(colConst);
        }
    }


    /**
     * Renders the game state to the window by filling in the interface elements
     * with relevant information.
     */
    private void renderState() {
        // Render the game board
        for (int i = 0; i < Vars.BOARD_ROW_COUNT; i++) {
            for (int j = 0; j < Vars.BOARD_COL_COUNT; j++) {
                PlayerMarks mark = this.currentBoard.getAt(i, j);
                this.buttons[i][j].setText(mark.toString());
            }
        }

        // Show the player that is to move this turn
        switch (this.currentBoard.getCurrentPlayer()) {
            case X -> {
                this.playerXBoxPane.setStyle("-fx-background-color: #90EE90; -fx-border-color: #D3D3D3;");
                this.playerOBoxPane.setStyle("-fx-background-color: WHITE; -fx-border-color: #D3D3D3;");
            }
            case O -> {
                this.playerXBoxPane.setStyle("-fx-background-color: WHITE; -fx-border-color: #D3D3D3;");
                this.playerOBoxPane.setStyle("-fx-background-color: #90EE90; -fx-border-color: #D3D3D3;");
            }
        }

        // Show current game score and rounds
        this.playerXScoreLabel.setText(String.valueOf(this.currentBoard.getPlayerXScore()));
        this.playerOScoreLabel.setText(String.valueOf(this.currentBoard.getPlayerOScore()));
        this.roundsLeftLabel.setText(String.valueOf((int) Math.ceil((float) this.currentBoard.getPliesLeft() / 2)));
    }


    /**
     * Calls {@code moveBot} on the current player if they are a bot.
     */
    private void callMoveNextPlayer() {
        // Get the bot to play this turn
        BotBase toPlay = switch (this.currentBoard.getCurrentPlayer()) {
            case X -> this.botX;
            case O -> this.botO;
            case EMPTY -> throw new RuntimeException();
        };

        if (toPlay != null) {
            // Send a message to the bot to make a move for this turn
            this.setButtonEnabled(false);
            this.moveBot(toPlay);
        } else {
            // This is a human player: enable game board buttons
            this.setButtonEnabled(true);
        }
    }


    /**
     * Process the coordinates of the button that the user selected on the game board.
     *
     * @param i The row number of the button clicked.
     * @param j The column number of the button clicked.
     */
    private void selectedCoordinates(int i, int j) {
        // Play the move on the board and update the display
        this.currentBoard.act(i, j);
        this.renderState();

        // Check if the game has ended
        if (this.currentBoard.getPliesLeft() == 0) {
            this.endOfGame();
        } else {
            this.callMoveNextPlayer();
        }
    }


    /**
     * Determine and announce the winner of the game.
     */
    private void endOfGame() {
        // Player X is the winner.
        if (this.currentBoard.getPlayerXScore() > this.currentBoard.getPlayerOScore()) {
            new Alert(Alert.AlertType.INFORMATION,
                    this.playerXName.getText() + " has won the game!").showAndWait();
            this.playerXBoxPane.setStyle("-fx-background-color: CYAN; -fx-border-color: #D3D3D3;");
            this.playerOBoxPane.setStyle("-fx-background-color: WHITE; -fx-border-color: #D3D3D3;");
            this.playerXName.setText(this.playerXName.getText() + " (Winner!)");
        }

        // Player O is the winner,
        else if (this.currentBoard.getPlayerOScore() > this.currentBoard.getPlayerXScore()) {
            new Alert(Alert.AlertType.INFORMATION,
                    this.playerOName.getText() + " has won the game!").showAndWait();
            this.playerXBoxPane.setStyle("-fx-background-color: WHITE; -fx-border-color: #D3D3D3;");
            this.playerOBoxPane.setStyle("-fx-background-color: CYAN; -fx-border-color: #D3D3D3;");
            this.playerOName.setText(this.playerOName.getText() + " (Winner!)");
        }

        // Player X and Player O tie.
        else {
            new Alert(Alert.AlertType.INFORMATION,
                    this.playerXName.getText() + " and " + this.playerOName.getText() + " have tied!").showAndWait();
            this.playerXBoxPane.setStyle("-fx-background-color: ORANGE; -fx-border-color: #D3D3D3;");
            this.playerOBoxPane.setStyle("-fx-background-color: ORANGE; -fx-border-color: #D3D3D3;");
        }

        // Disable the game board buttons to prevent from playing further.
        this.setButtonEnabled(false);
    }


    /**
     * Close OutputFrame controlled by OutputFrameController if end game button is clicked.
     */
    @FXML
    private void endGame() {
        System.exit(0);
    }


    /**
     * Reopen InputFrame controlled by InputFrameController if play new game button is clicked.
     */
    @FXML
    private void playNewGame() throws IOException {
        // Close secondary stage/output frame.
        Stage secondaryStage = (Stage) this.gameBoardPane.getScene().getWindow();
        secondaryStage.close();

        // Reopen primary stage/input frame.
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("InputFrame.fxml"));
        Parent root = loader.load();
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Adjacency Gameplay");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void moveBot(BotBase bot) {
        BotMoveTask moveTask = new BotMoveTask(bot, this.currentBoard);
        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(Vars.MOVE_MAX_TIME), event -> bot.stop()));

        // If the move task succeeds, play the move on the board
        moveTask.setOnSucceeded(event -> {
            timer.stop();

            int[] botMove = moveTask.getValue();
            int i = botMove[0];
            int j = botMove[1];

            try {
                this.selectedCoordinates(i, j);
            } catch (IllegalStateException e) {
                new Alert(Alert.AlertType.ERROR, "Bot Invalid Coordinates. Exiting.").showAndWait();
                System.exit(1);
            }
        });

        // If the move task fails, stop the program immediately
        moveTask.setOnFailed(event -> {
            timer.stop();

            Throwable e = moveTask.getException();
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, String.format("A fatal error has occured: %n%s%n%n Exiting.", e.getMessage())).showAndWait();
            System.exit(1);
        });

        new Thread(moveTask).start();
        timer.play();
    }

    private void setButtonEnabled(boolean b) {
        for (int i = 0; i < Vars.BOARD_ROW_COUNT; i++)
            for (int j = 0; j < Vars.BOARD_COL_COUNT; j++)
                this.buttons[i][j].setDisable(!b);
    }

    private static class BotMoveTask extends Task<int[]> {
        private final BotBase bot;
        private final Board board;

        public BotMoveTask(BotBase bot, Board board) {
            this.bot = bot;
            this.board = board;
        }

        @Override
        public int[] call() {
            return this.bot.move(this.board);
        }
    }
}