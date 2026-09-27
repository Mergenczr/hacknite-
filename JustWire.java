import java.awt.*;

public class JustWire extends CircuitComponent {

    private boolean vertical = false;

    public JustWire(int x, int y, String name) {
        super(x, y, 120, 40, name, 0);
    }

    public void rotate() {
        vertical = !vertical;

        if (vertical) {
            width = 40;
            height = 120;
        } else {
            width = 120;
            height = 40;
        }
    }

    public boolean isVertical() {
        return vertical;
    }

    @Override
    public void draw(Graphics2D g) {
        drawSelection(g);
        g.setColor(Color.BLACK);
        drawSymbol(g);
    }

    public void drawSymbol(Graphics2D g) {
        g.setColor(Color.BLACK);

        if (!vertical) {
            int centerY = y + height / 2;

            g.drawLine(x, centerY, x + width, centerY);
            g.fillOval(x - 5, centerY - 5, 10, 10);
            g.fillOval(x + width - 5, centerY - 5, 10, 10);

        } else {
            int centerX = x + width / 2;

            g.drawLine(centerX, y, centerX, y + height);
            g.fillOval(centerX - 5, y - 5, 10, 10);
            g.fillOval(centerX - 5, y + height - 5, 10, 10);
        }
    }

    @Override
    public String getUnit() {
        return "";
    }

    @Override
    public String getValueName() {
        return "wire";
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
