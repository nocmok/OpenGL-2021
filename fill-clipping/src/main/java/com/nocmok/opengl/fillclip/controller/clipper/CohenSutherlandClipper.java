package com.nocmok.opengl.fillclip.controller.clipper;

import com.nocmok.opengl.fillclip.util.IntPoint;
import com.nocmok.opengl.fillclip.util.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class CohenSutherlandClipper {

    // | left | right | up | down |
    private static final int LEFT = 8;
    private static final int RIGHT = 4;
    private static final int TOP = 2;
    private static final int BOTTOM = 1;
    private static final int CENTER = 0;

    public CohenSutherlandClipper() {

    }

    private int encodePoint(IntPoint point, Rectangle window) {
        int code = 0;
        if (point.x < window.x) {
            code |= LEFT;
        } else if (point.x > window.x2()) {
            code |= RIGHT;
        }
        if (point.y < window.y) {
            code |= TOP;
        } else if (point.y > window.y2()) {
            code |= BOTTOM;
        }
        return code;
    }

    public List<IntPoint[]> clip(List<IntPoint[]> lines, Rectangle w) {
        var clipped = new ArrayList<IntPoint[]>();
        var toClip = new ArrayList<IntPoint[]>();
        for (var line : lines) {
            int p1Code = encodePoint(line[0], w);
            int p2Code = encodePoint(line[1], w);
            if ((p1Code & p2Code) > 0) {
                continue;
            }
            if (p1Code == 0 && p2Code == 0) {
                clipped.add(line);
            } else {
                toClip.add(line);
            }
        }
        for (var line : toClip) {
            var points = new ArrayList<IntPoint>();
            for (var p : line) {
                int pCode = encodePoint(p, w);
                if (pCode == 0) {
                    points.add(p);
                } else if ((pCode & TOP) != 0 && checkBounds(getXByY(line, w.y), w.x, w.x2())) {
                    points.add(new IntPoint(getXByY(line, w.y), w.y));
                } else if ((pCode & LEFT) != 0 && checkBounds(getYByX(line, w.x), w.y, w.y2())) {
                    points.add(new IntPoint(w.x, getYByX(line, w.x)));
                } else if ((pCode & BOTTOM) != 0 && checkBounds(getXByY(line, w.y2()), w.x, w.x2())) {
                    points.add(new IntPoint(getXByY(line, w.y2()), w.y2()));
                } else if ((pCode & RIGHT) != 0 && checkBounds(getYByX(line, w.x2()), w.y, w.y2())) {
                    points.add(new IntPoint(w.x2(), getYByX(line, w.x2())));
                }
            }
            if (!points.isEmpty()) {
                clipped.add(new IntPoint[]{points.get(0), points.get(1)});
            }
        }
        return clipped;
    }

    private int getYByX(IntPoint[] line, int x) {
        int dx = line[1].x - line[0].x;
        int dy = line[1].y - line[0].y;
        if (dx == 0) {
            return Integer.MAX_VALUE;
        }
        double k = (double) dy / dx;
        double b = line[0].y - k * line[0].x;
        return (int) Math.round(k * x + b);
    }

    private int getXByY(IntPoint[] line, int y) {
        int dx = line[1].x - line[0].x;
        int dy = line[1].y - line[0].y;
        if (dx == 0) {
            return line[0].x;
        }
        double k = (double) dy / dx;
        double b = line[0].y - k * line[0].x;
        return (int) Math.round((y - b) / k);
    }

    private boolean checkBounds(int value, int min, int max) {
        return value >= min && value <= max;
    }
}
