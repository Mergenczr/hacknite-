import java.awt.*;

public class Resistor extends CircuitComponent {

    private boolean vertical = false;

    public Resistor(int x, int y, String name, double resistance) {
        super(x, y, 120, 60, name, resistance);
    }

    public void rotate() {
        vertical = !vertical;

        if (vertical) {
            width = 60;
            height = 120;
        } else {
            width = 120;
            height = 60;
        }
    }

    public boolean isVertical() {
        return vertical;
    }

    @Override
    public void draw(Graphics2D g) {
        drawSelection(g);
        g.setColor(Color.BLACK);

        if (!name.isEmpty()) {
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString(
                    name + " = " + String.format("%.2f", value) + " Ω",
                    x,
                    y - 5
            );
        }

        drawSymbol(g);
    }

    public void drawSymbol(Graphics2D g) {
        g.setColor(Color.BLACK);

        if (!vertical) {
            int centerY = y + height / 2;
            int resistorStart = x + 20;
            int resistorEnd = x + width - 20;

            g.drawLine(x, centerY, resistorStart, centerY);

            int section = (resistorEnd - resistorStart) / 8;
            int[] xPoints = new int[9];
            int[] yPoints = new int[9];

            xPoints[0] = resistorStart;
            yPoints[0] = centerY;

            for (int i = 1; i < 9; i++) {
                xPoints[i] = resistorStart + i * section;

                if (i == 8) {
                    yPoints[i] = centerY;
                } else if (i % 2 == 1) {
                    yPoints[i] = centerY - 12;
                } else {
                    yPoints[i] = centerY + 12;
                }
            }

            g.drawPolyline(xPoints, yPoints, xPoints.length);
            g.drawLine(resistorEnd, centerY, x + width, centerY);

            g.fillOval(x - 5, centerY - 5, 10, 10);
            g.fillOval(x + width - 5, centerY - 5, 10, 10);

        } else {
            int centerX = x + width / 2;
            int resistorStart = y + 20;
            int resistorEnd = y + height - 20;

            g.drawLine(centerX, y, centerX, resistorStart);

            int section = (resistorEnd - resistorStart) / 8;
            int[] xPoints = new int[9];
            int[] yPoints = new int[9];

            xPoints[0] = centerX;
            yPoints[0] = resistorStart;

            for (int i = 1; i < 9; i++) {
                yPoints[i] = resistorStart + i * section;

                if (i == 8) {
                    xPoints[i] = centerX;
                } else if (i % 2 == 1) {
                    xPoints[i] = centerX - 12;
                } else {
                    xPoints[i] = centerX + 12;
                }
            }

            g.drawPolyline(xPoints, yPoints, xPoints.length);
            g.drawLine(centerX, resistorEnd, centerX, y + height);

            g.fillOval(centerX - 5, y - 5, 10, 10);
            g.fillOval(centerX - 5, y + height - 5, 10, 10);
        }
    }

    @Override
    public String getUnit() {
        return "Ω";
    }

    @Override
    public String getValueName() {
        return "resistance";
    }

    @Override
    public int getTerminalX(int terminal) {
        if (!vertical) {
            return terminal == 0 ? x : x + width;
        }

        return x + width / 2;
    }

    @Override
    public int getTerminalY(int terminal) {
        if (!vertical) {
            return y + height / 2;
        }

        return terminal == 0 ? y : y + height;
    }
}
