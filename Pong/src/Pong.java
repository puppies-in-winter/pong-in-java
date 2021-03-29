import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.awt.*;

public class Pong extends StackPane {

    private Pane field;

    private HBox scoreBox;

    private VBox wordsBox;

    private BorderPane scoreAndField;

    private Label playerLabel, opponentLabel, winnerLabel, pongTitle, pressSpaceToStart, pressQToPause, pressESCToQuit, howToControl, pressTForInstructions, gamePrompt;

    private Rectangle playerRect;
    private Rectangle opponentRect;

    private Circle ball;

    private Line barrier;

    private int playerScore;
    private int opponentScore;

    private Timeline GAMEPLAY_LOOP;
    private Timeline SCORE_CHECKER;

    private boolean isPaused = false;
    private boolean isGameOver = false;
    private boolean hasGameStarted = false;
    private boolean isOnInstructionsPage = false;
    private String opponentType = "COMPUTER";
    private String player1Movement = "MOUSE";

    private int p1MoveQuantity = 0;
    private int p2MoveQuantity = 0;

    private boolean mustPause = true;
    // I have this intersects boolean so that it doesn't bounce off the rectangles more than once
    private boolean intersects = false;
    // I have this hitsTopOrBottom boolean so that it doesn't bounce off those borders more than once
    private boolean hitsTopOrBottom = false;

    public Pong(){

        // Initialize scores
        playerScore = 0;
        opponentScore = 0;

        // Make the labels for scores
        playerLabel = new Label(""+playerScore);
        playerLabel.setFont(new Font("Nova Square", 24));
        playerLabel.setTextFill(Color.CADETBLUE);
        opponentLabel = new Label(""+opponentScore);
        opponentLabel.setFont(new Font("Nova Square", 24));
        opponentLabel.setTextFill(Color.INDIANRED);

        // Make the label for showing the winner
        winnerLabel = new Label("");

        // Make the label for the title
        pongTitle = new Label("PONG");
        pongTitle.setFont(new Font("Nova Square", 50));
        pongTitle.setTextFill(Color.WHITE);
        pongTitle.setAlignment(Pos.CENTER);

        // Make the label for the prompt to start the game
        pressSpaceToStart = new Label("Press SPACE to Start");
        pressSpaceToStart.setFont(new Font("Nova Square", 24));
        pressSpaceToStart.setTextFill(Color.WHITE);
        pressSpaceToStart.setAlignment(Pos.CENTER);

        // Make the label for the prompt to pause the game
        pressQToPause = new Label("Press Q at Any Time to Pause");
        pressQToPause.setFont(new Font("Nova Square", 24));
        pressQToPause.setTextFill(Color.WHITE);
        pressQToPause.setAlignment(Pos.CENTER);

        // Make the label for the prompt to quit the game
        pressESCToQuit = new Label("Press ESCAPE at Any Time to Quit");
        pressESCToQuit.setFont(new Font("Nova Square", 24));
        pressESCToQuit.setTextFill(Color.WHITE);
        pressESCToQuit.setAlignment(Pos.CENTER);

        // Make the label for the prompt to switch back and forth from instructions to main menu
        pressTForInstructions = new Label("Press T for the Instructions");
        pressTForInstructions.setFont(new Font("Nova Square", 24));
        pressTForInstructions.setTextFill(Color.WHITE);
        pressTForInstructions.setAlignment(Pos.CENTER);

        // Make the label for telling how to move your paddle
        howToControl = new Label("Moving with Mouse, SinglePlayer Only (Press Y to Change)");
        howToControl.setFont(new Font("Nova Square", 24));
        howToControl.setTextFill(Color.WHITE);
        howToControl.setAlignment(Pos.CENTER);

        // Make the label for how the game goes
        gamePrompt = new Label("Play against the computer, first to 10 wins (Press U to Change)");
        gamePrompt.setFont(new Font("Nova Square", 24));
        gamePrompt.setTextFill(Color.WHITE);
        gamePrompt.setAlignment(Pos.CENTER);

        // Make the player's rectangle
        playerRect = new Rectangle(15,300,10,100);
        playerRect.setFill(Color.CADETBLUE);
        playerRect.setStroke(Color.MIDNIGHTBLUE);

        // Make the opponent's rectangle
        opponentRect = new Rectangle(775,300,10,100);
        opponentRect.setFill(Color.INDIANRED);
        opponentRect.setStroke(Color.DARKRED);

        // Make the circle thing
        ball = new Circle(10, Color.WHITE);
        ball.setLayoutX(400);
        ball.setLayoutY(400);

        // Make the line that goes down the center
        barrier = new Line(400,50,400,800);
        barrier.setStrokeWidth(5);
        barrier.setStroke(Color.WHITE);
        barrier.getStrokeDashArray().addAll(10.0,10.0,10.0,10.0);

        // Make the HBox containing the labels of scores
        scoreBox = new HBox();

        // Make the VBox containing all the words for the prompts and such
        wordsBox = new VBox();
        wordsBox.getChildren().addAll(pongTitle, pressSpaceToStart, pressTForInstructions);
        wordsBox.setAlignment(Pos.CENTER);
        wordsBox.setSpacing(30);

        // Make the BorderPane that has everything in it
        scoreAndField = new BorderPane();

        // Make the Pane that houses the rectangles
        field = new Pane();
        field.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));


        // This will be what controls the gameplay including the bounce, moving the ball,
        // moving the opponent's rectangle
        GAMEPLAY_LOOP = new Timeline(new KeyFrame(Duration.millis(8), new EventHandler<ActionEvent>() {

            double deltaX = 3;
            double deltaY = (Math.random()*1) + 1.5;
            int willDeltaYBeNegative = (int)(Math.random() * 2);

            // This is the random factor that, along with the ball itself, will affect how the computer moves
            double randomFactor = (Math.random()*163) + 287;

            @Override
            public void handle(ActionEvent event) {

                if (hasGameStarted) {

                    // Moving the ball
                    ball.setLayoutX(ball.getLayoutX() - deltaX);
                    ball.setLayoutY(ball.getLayoutY() - deltaY);

                    // Checking if the ball is at the top, left, bottom, or right borders
                    Bounds bounds = field.getBoundsInLocal();
                    boolean atRightBorder = ball.getLayoutX() >= (bounds.getMaxX() - ball.getRadius());
                    boolean atLeftBorder = ball.getLayoutX() <= (bounds.getMinX() + ball.getRadius());
                    boolean atBottomBorder = ball.getLayoutY() >= (bounds.getMaxY() - 40 - ball.getRadius());
                    boolean atTopBorder = ball.getLayoutY() <= (bounds.getMinY() + 40 + ball.getRadius());

                    // This will move the opponentRect to follow the ball
                    if (ball.getLayoutY() < 600 && ball.getLayoutY() > 150 && opponentType.equals("COMPUTER")) {
                        opponentRect.setLayoutY(ball.getLayoutY() - randomFactor);
                    }

                    // These will check if the ball is touching either of the rectangles
                    boolean atPlayerRect = playerRect.intersects(playerRect.sceneToLocal(ball.localToScene(ball.getBoundsInLocal())));
                    boolean atOpponentRect = opponentRect.intersects(opponentRect.sceneToLocal(ball.localToScene(ball.getBoundsInLocal())));

                    if ((atPlayerRect || atOpponentRect) && !intersects) {
                        deltaY = (Math.random()*1) + 1.5;
                        if (willDeltaYBeNegative == 0){
                            deltaY *= -1;
                        }
                        willDeltaYBeNegative = (int)(Math.random() * 2);
                        deltaX *= -1;
                        intersects = true;
                        if (atOpponentRect){
                            new java.util.Timer().schedule(
                                    new java.util.TimerTask() {
                                        @Override
                                        public void run() {
                                            randomFactor = (Math.random()*163) + 287;
                                        }
                                    }, 500
                            );
                        }
                    }
                    if (atBottomBorder || atTopBorder) {
                        deltaY *= -1;
                    }

                    if (atRightBorder) {
                        playerScore++;
                        deltaY = (Math.random()*1) + 1.5;
                        if (willDeltaYBeNegative == 0){
                            deltaY *= -1;
                        }
                        willDeltaYBeNegative = (int)(Math.random() * 2);
                        deltaX *= -1;
                        playerLabel.setText("" + playerScore);
                        ball.setLayoutX(400);
                        ball.setLayoutY(400);
                        mustPause = true;
                        randomFactor = (Math.random()*163) + 287;
                    }

                    if (atLeftBorder) {
                        opponentScore++;
                        deltaY = (Math.random()*1) + 1.5;
                        if (willDeltaYBeNegative == 0){
                            deltaY *= -1;
                        }
                        willDeltaYBeNegative = (int)(Math.random() * 2);
                        deltaX *= -1;
                        opponentLabel.setText("" + opponentScore);
                        ball.setLayoutX(400);
                        ball.setLayoutY(400);
                        mustPause = true;
                        randomFactor = (Math.random()*163) + 287;
                    }

                    // Now if it has intersected, after 0.50 seconds, change it back to false
                    if (intersects) {
                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        intersects = false;
                                    }
                                }, 500
                        );
                    }

                    if (mustPause) {
                        GAMEPLAY_LOOP.pause();
                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        GAMEPLAY_LOOP.play();
                                        mustPause = false;
                                    }
                                }, 1500
                        );
                    }

                }

            }


        }));

        // SCORE_CHECKER timeline will check and change scores
        SCORE_CHECKER = new Timeline(new KeyFrame(Duration.millis(8), new EventHandler<>() {

            @Override
            public void handle(ActionEvent event) {
                if (playerScore == 10) {

                    if (opponentType.equals("COMPUTER")) {
                        winnerLabel.setText("YOU WON!");
                        winnerLabel.setLayoutX(325);
                        winnerLabel.setLayoutY(200);
                    } else {
                        winnerLabel.setText("PLAYER 1 WON");
                        winnerLabel.setLayoutX(300);
                        winnerLabel.setLayoutY(200);
                    }
                    winnerLabel.setFont(new Font("Nova Square",30));
                    winnerLabel.setTextFill(Color.CADETBLUE);
                    isGameOver = true;
                    hasGameStarted = false;
                    GAMEPLAY_LOOP.stop();
                    SCORE_CHECKER.stop();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    field.getChildren().removeAll(ball, playerRect, opponentRect, barrier);
                    wordsBox.getChildren().add(pressTForInstructions);
                    pressTForInstructions.setText("Press T to Go Back to the Menu");
                    pressTForInstructions.setLayoutX(210);
                    p1MoveQuantity = 0;
                    p2MoveQuantity = 0;

                }

                if (opponentScore == 10) {

                    if (opponentType.equals("COMPUTER")) {
                        winnerLabel.setText("COMPUTER WON");
                    } else {
                        winnerLabel.setText("PLAYER 2 WON");
                    }
                    winnerLabel.setFont(new Font("Nova Square",30));
                    winnerLabel.setTextFill(Color.INDIANRED);
                    isGameOver = true;
                    hasGameStarted = false;
                    GAMEPLAY_LOOP.stop();
                    SCORE_CHECKER.stop();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    field.getChildren().removeAll(ball, playerRect, opponentRect, barrier);
                    wordsBox.getChildren().add(pressTForInstructions);
                    pressTForInstructions.setText("Press T to Go Back to the Menu");
                    pressTForInstructions.setLayoutX(200);
                    winnerLabel.setLayoutX(275);
                    winnerLabel.setLayoutY(200);
                    p1MoveQuantity = 0;
                    p2MoveQuantity = 0;
                }
            }
        }));


        // Add these to the greater BorderPane of scoreAndField
        scoreAndField.setTop(scoreBox);
        scoreAndField.setCenter(field);

        // Add the scoreAndField and the wordsBox to the Pong
        this.getChildren().addAll(scoreAndField, wordsBox);

        // Adding mouse and key events to the greater BorderPane
        this.setOnMouseMoved(new MouseHandler());
        this.setOnKeyPressed(new KeysHandler());

        this.setFocusTraversable(true);

    }

    public class MouseHandler implements EventHandler<MouseEvent>{

        PointerInfo mouse = MouseInfo.getPointerInfo();
        Point mouseLocation = mouse.getLocation(); // this is the location of the mouse on the entire screen
        double mouseStayX = mouseLocation.getX();
        double mouseStayY = mouseLocation.getY();

        @Override
        public void handle(MouseEvent event) {
            // Robot rbt will keep the mouse in the game
            Robot rbt = null;
            try {
                rbt = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
            }
            assert rbt != null;

            mouse = MouseInfo.getPointerInfo();
            mouseLocation = mouse.getLocation();

            if (!isPaused) {
                mouseStayX = mouseLocation.getX();
                mouseStayY = mouseLocation.getY();
            }


            // Only want the mouse to move if the game is not paused, the MOUSE option is chosen, and game has started
            if (!isPaused && player1Movement.equals("MOUSE") && hasGameStarted) {
                // If the mouse is within these Y coordinate bounds, then move the playerRect
                if (event.getY() > 50 && event.getY() < 750) {
                    playerRect.setLayoutY(event.getY() - 350);
                }
                mouseStayX = mouseLocation.getX();
                mouseStayY = mouseLocation.getY();
            }

            // If it's paused or game over then make the mouse stay put so that the playerRect can't move
            if (isPaused || isGameOver){
                rbt.mouseMove((int) mouseStayX, (int) mouseStayY);
            }
            // These next few if statements will keep the mouse within the game
            if (event.getY() < 50) {
                rbt.mouseMove((int) mouseLocation.getX(), (int) mouseLocation.getY() + 1);
            }
            if (event.getY() > 800) {
                rbt.mouseMove((int) mouseLocation.getX(), (int) mouseLocation.getY() - 10);
            }
            if (event.getX() < 100) {
                rbt.mouseMove((int) mouseLocation.getX() + 100, (int) mouseLocation.getY());
            }
            if (event.getX() > 750) {
                rbt.mouseMove((int) mouseLocation.getX() - 100, (int) mouseLocation.getY());
            }


        }
    }

    public class KeysHandler implements EventHandler<KeyEvent>{

        @Override
        public void handle(KeyEvent event) {


            // The ESCAPE Key will let us quit the game
            if (event.getCode() == KeyCode.ESCAPE){
                System.exit(1);
            }

            // The Q Key will let us pause the game whenever
            if (event.getCode() == KeyCode.Q){
                if (!isPaused && hasGameStarted) {
                    GAMEPLAY_LOOP.pause();
                    isPaused = true;
                } else if (isPaused && hasGameStarted){
                    GAMEPLAY_LOOP.play();
                    isPaused = false;
                }
            }

            // The W Key will move the Player1 up if player1Movement is set to KEYBOARD
            if (event.getCode() == KeyCode.W && player1Movement.equals("KEYBOARD") && !isPaused && hasGameStarted){
                if (p1MoveQuantity > -16) {
                    playerRect.setLayoutY(playerRect.getLayoutY() - 40);
                    p1MoveQuantity -= 2;
                }
            }
            // The S Key will move the Player1 down if player1Movement is set to KEYBOARD
            if (event.getCode() == KeyCode.S && player1Movement.equals("KEYBOARD") && !isPaused && hasGameStarted){
                if (p1MoveQuantity < 20) {
                    playerRect.setLayoutY(playerRect.getLayoutY() + 40);
                    p1MoveQuantity += 2;
                }
            }

            // The UP ARROW Key will move the Player2 up if opponentType is set to PLAYER
            if (event.getCode() == KeyCode.UP && opponentType.equals("PLAYER") && !isPaused && hasGameStarted){
                if (p2MoveQuantity > -16) {
                    opponentRect.setLayoutY(opponentRect.getLayoutY() - 40);
                    p2MoveQuantity -= 2;
                }
            }
            // The DOWN ARROW Key will move the Player2 down if opponentType is set to PLAYER
            if (event.getCode() == KeyCode.DOWN && opponentType.equals("PLAYER") && !isPaused && hasGameStarted){
                if (p2MoveQuantity < 20) {
                    opponentRect.setLayoutY(opponentRect.getLayoutY() + 40);
                    p2MoveQuantity += 2;
                }
            }
            
            // The SPACEBAR will begin the game
            if (event.getCode() == KeyCode.SPACE && !isOnInstructionsPage && !hasGameStarted){

                if (!scoreBox.getChildren().contains(playerLabel) && !scoreBox.getChildren().contains(opponentLabel)) {

                    scoreBox.getChildren().addAll(playerLabel, opponentLabel);
                    scoreBox.setAlignment(Pos.CENTER);
                    scoreBox.setPadding(new Insets(20, 20, 20, 20));
                    scoreBox.setSpacing(60);
                    scoreBox.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

                }

                field.getChildren().addAll(playerRect, opponentRect, barrier, ball, winnerLabel);
                winnerLabel.setText("");
                wordsBox.getChildren().removeAll(pongTitle, pressSpaceToStart, pressESCToQuit, pressQToPause, pressTForInstructions);
                playerLabel.setText("" + playerScore);
                opponentLabel.setText("" + opponentScore);

                playerRect.relocate(10,300);
                opponentRect.relocate(780,300);

                // Loops the gameplay loop FOREVER
                GAMEPLAY_LOOP.setCycleCount(Timeline.INDEFINITE);
                SCORE_CHECKER.setCycleCount(Timeline.INDEFINITE);
                GAMEPLAY_LOOP.play();
                SCORE_CHECKER.play();

                isOnInstructionsPage = false;
                hasGameStarted = true;

            }

            // The T Key will show us the instructions page and also the main menu
            if (event.getCode() == KeyCode.T){

                if (!isOnInstructionsPage && !hasGameStarted && !isGameOver) {

                    wordsBox.getChildren().removeAll(pongTitle, pressSpaceToStart, pressTForInstructions);
                    wordsBox.getChildren().addAll(gamePrompt, howToControl, pressQToPause, pressESCToQuit, pressTForInstructions);
                    pressTForInstructions.setText("Press T to Go Back to the Menu");
                    isOnInstructionsPage = true;

                } else if (isOnInstructionsPage && !hasGameStarted && !isGameOver){

                    wordsBox.getChildren().removeAll(pressQToPause, pressESCToQuit, howToControl, gamePrompt, pressTForInstructions);
                    wordsBox.getChildren().addAll(pongTitle, pressSpaceToStart, pressTForInstructions);
                    pressTForInstructions.setText("Press T for the Instructions");
                    isOnInstructionsPage = false;

                } else if (isGameOver){

                    wordsBox.getChildren().removeAll(winnerLabel, pressTForInstructions);
                    wordsBox.getChildren().addAll(pongTitle, pressSpaceToStart, pressTForInstructions);
                    field.getChildren().removeAll(playerRect, opponentRect, barrier, ball, winnerLabel);
                    winnerLabel.setText("");
                    playerScore = 0;
                    opponentScore = 0;
                    playerLabel.setText("");
                    opponentLabel.setText("");
                    pressTForInstructions.setText("Press T for the Instructions");
                    isOnInstructionsPage = false;
                    isGameOver = false;
                    hasGameStarted = false;
                    mustPause = true;

                }

            }

            if (event.getCode() == KeyCode.Y){

                if (isOnInstructionsPage && !hasGameStarted && !isGameOver && player1Movement.equals("MOUSE")){
                    howToControl.setText("P1 moves with W and S (Press Y to Change)");
                    gamePrompt.setText("Play against the computer, first to 10 wins (Press U to Change)");
                    player1Movement = "KEYBOARD";

                }
                else if (isOnInstructionsPage && !hasGameStarted && !isGameOver && player1Movement.equals("KEYBOARD")){
                    howToControl.setText("Moving with Mouse, SinglePlayer Only (Press Y to Change)");
                    gamePrompt.setText("Play against the computer, first to 10 wins (Press U to Change)");
                    player1Movement = "MOUSE";
                    opponentType = "COMPUTER";
                }

            }

            // The U Key will let us change the opponent type
            if (event.getCode() == KeyCode.U){
                if (isOnInstructionsPage && !hasGameStarted && !isGameOver && opponentType.equals("COMPUTER")){
                    howToControl.setText("P1 -- W,S || P2 -- UP,DOWN (Press Y to Change)");
                    gamePrompt.setText("1 V 1, first to 10 wins (Press U to Change)");
                    player1Movement = "KEYBOARD";
                    opponentType = "PLAYER";
                }
                else if (isOnInstructionsPage && !hasGameStarted && !isGameOver && opponentType.equals("PLAYER")){
                    howToControl.setText("P1 moves with W and S (Press Y to Change)");
                    gamePrompt.setText("Play against the computer, first to 10 wins (Press U to Change)");
                    player1Movement = "KEYBOARD";
                    opponentType = "COMPUTER";
                }
            }

            // For testing purposes
            if (event.getCode() == KeyCode.O){
                playerScore = 10;
                playerLabel.setText(""+playerScore);
            }
            if (event.getCode() == KeyCode.P){
                opponentScore = 10;
                opponentLabel.setText(""+opponentScore);
            }

        }
    }

}
