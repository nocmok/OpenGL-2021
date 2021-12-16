package com.nocmok.opengl.curve.controller;

import com.nocmok.opengl.curve.controller.action.BezierCurvePivotHandler;
import com.nocmok.opengl.curve.controller.action.LinearCurvePivotHandler;
import com.nocmok.opengl.curve.controller.control.CanvasGrid;
import com.nocmok.opengl.curve.controller.control.Pivot;
import com.nocmok.opengl.curve.controller.control.PixelatedCanvas;
import com.nocmok.opengl.curve.curve_drawer.BSpline3;
import com.nocmok.opengl.curve.curve_drawer.LinearCurve;
import com.nocmok.opengl.curve.util.Point;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

import javax.sound.sampled.Line;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class BSplineController extends AbstractController {

    @FXML
    private GridPane root;
    @FXML
    private Pane frame;
    @FXML
    private HBox header;
    @FXML
    private Button clear;
    @FXML
    private ScrollPane scroll;
    @FXML
    private Button about;
    private Canvas canvas;

    @Override public Parent getRoot() {
        return root;
    }

    private String getAboutMessage() {
        try {
            var in = getClass().getClassLoader().getResourceAsStream("About.txt");
            return in == null ? null : new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ignore) {
        }
        return null;
    }

    private void clearCanvas(Canvas canvas) {
        var gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
    }

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {

        var screen = Screen.getPrimary().getBounds();
        canvas = new Canvas(screen.getWidth(), screen.getHeight());
        clearCanvas(canvas);
        frame.getChildren().add(canvas);

        scroll.addEventFilter(ScrollEvent.SCROLL, e -> {
            scroll.setHvalue(scroll.getHvalue() - e.getDeltaX() / scroll.getWidth());
            scroll.setVvalue(scroll.getVvalue() - e.getDeltaY() / scroll.getHeight());
            e.consume();
        });

        String aboutMessage = Objects.requireNonNullElse(getAboutMessage(), "Cannot load about message");
        about.setOnMouseClicked(e -> {
            var alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About this program");
            alert.setHeaderText(null);
            alert.setContentText(aboutMessage);
            alert.setResizable(true);
            alert.getDialogPane().setMinWidth(600);
            alert.showAndWait();
        });

        double step = 1e-2;
        var bSpline = new BSpline3(new CanvasGrid(canvas, Color.ROYALBLUE, 3), step);
        var linearInterpolation = new LinearCurve(new CanvasGrid(canvas, Color.LIGHTGRAY, 1));

        var pivotsHandler = new LinearCurvePivotHandler() {
            @Override public void onPivotsChange(Collection<Pivot> pivots) {
                clearCanvas(canvas);
                var points = pivots.stream()
                        .map(p -> new Point(p.x(), p.y()))
                        .collect(Collectors.toList());
                linearInterpolation.drawCurve(points);
                if (points.size() >= 4) {
                    bSpline.drawCurve(points);
                }
            }
        };
        pivotsHandler.attach(frame);

        clear.setOnMouseClicked(e -> {
            clearCanvas(canvas);
            pivotsHandler.getPivots().clear();
            frame.getChildren().clear();
            frame.getChildren().add(canvas);
        });
    }
}

