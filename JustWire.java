import java.awt.*;

public class JustWire extends CircuitComponent {

    public JustWire(
            int x,
            int y,
            String name
    ) {

        super(
                x,
                y,
                120,
                40,
                name,
                0
        );
    }


    @Override
    public void draw(Graphics2D g) {

        drawSelection(g);

        g.setColor(Color.BLACK);

        drawSymbol(g);
    }


    public void drawSymbol(Graphics2D g) {

        int centerY = y + 20;

        g.drawLine(
                x,
                centerY,
                x + width,
                centerY
        );

        g.fillOval(
                x - 5,
                centerY - 5,
                10,
                10
        );

        g.fillOval(
                x + width - 5,
                centerY - 5,
                10,
                10
        );
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

        if (terminal == 0) {
            return x;
        }

        return x + width;
    }


    @Override
    public int getTerminalY(int terminal) {

        return y + 20;
    }
}