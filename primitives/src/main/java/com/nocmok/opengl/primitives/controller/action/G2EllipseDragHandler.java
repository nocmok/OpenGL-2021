package com.nocmok.opengl.primitives.controller.action;

import com.nocmok.opengl.primitives.controller.control.PixelatedCanvas;
import com.nocmok.opengl.primitives.util.Rectangle;
import javafx.scene.input.MouseEvent;

import java.awt.Color;

public class G2EllipseDragHandler extends ShapeDragHandler {

    private PixelatedCanvas canvas;

    private PixelatedCanvas.Graphics2DWrapper g2;

    private int dragX0;

    private int dragY0;

    private int dragX1;

    private int dragY1;

    @Override public void attach(PixelatedCanvas canvas) {
        super.attach(canvas);
        this.canvas = canvas;
        this.g2 = canvas.createGraphicsWrapper();
    }

    @Override public void onDragStarted(MouseEvent e) {
        dragX0 = dragX1 = canvas.toPixelX(e.getX());
        dragY0 = dragY1 = canvas.toPixelY(e.getY());
    }

    private Rectangle getEllipseAreaByCapture(int x0, int y0, int x1, int y1) {
        return Rectangle.ofPoints(x0, y0, x1, y1);
    }

    @Override public void onDrag(MouseEvent e) {
        int newDragX1 = canvas.toPixelX(e.getX());
        int newDragY1 = canvas.toPixelY(e.getY());
        if (newDragX1 == dragX1 && newDragY1 == dragY1) {
            return;
        }

        var oldEllipseArea = getEllipseAreaByCapture(dragX0, dragY0, dragX1, dragY1);
        var newEllipseArea = getEllipseAreaByCapture(dragX0, dragY0, newDragX1, newDragY1);

        var oldActualArea = Rectangle.ofSize(oldEllipseArea.x, oldEllipseArea.y, oldEllipseArea.w + 1, oldEllipseArea.h + 1);
        var newActualArea = Rectangle.ofSize(newEllipseArea.x, newEllipseArea.y, newEllipseArea.w + 1, newEllipseArea.h + 1);

        g2.fillRect(oldActualArea, Color.WHITE);
        g2.drawEllipse(newEllipseArea.x, newEllipseArea.y, newEllipseArea.w, newEllipseArea.h, Color.BLACK);
        g2.flushRect(oldActualArea.add(newActualArea));

        dragX1 = newDragX1;
        dragY1 = newDragY1;
    }
}
