import java.awt.*;

public class VoltageSource
        extends CircuitComponent {

    public VoltageSource(
            int x,
            int y,
            String name,
            double voltage
    ) {

        super(
                x,
                y,
                80,
                80,
                name,
                voltage
        );
    }


    @Override
    public void draw(
            Graphics2D g
    ) {

        drawSelection(g);

        g.setColor(Color.BLACK);

        if (!name.isEmpty()) {

            g.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            14
                    )
            );

            g.drawString(
                    name +
                    " = " +
                    String.format(
                            "%.2f",
                            value
                    ) +
                    " V",
                    x,
                    y - 5
            );
        }

        drawSymbol(g);
    }


    public void drawSymbol(
            Graphics2D g
    ) {

        g.setColor(Color.BLACK);

        int centerX =
                x + 40;

        int centerY =
                y + 40;


        g.drawLine(
                centerX,
                y,
                centerX,
                centerY - 25
        );

        g.drawLine(
                centerX,
                centerY + 25,
                centerX,
                y + 80
        );


        g.drawOval(
                centerX - 25,
                centerY - 25,
                50,
                50
        );


        g.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );


        g.drawString(
                "+",
                centerX - 5,
                centerY - 6
        );

        g.drawString(
                "-",
                centerX - 4,
                centerY + 18
        );


        g.fillOval(
                centerX - 5,
                y - 5,
                10,
                10
        );

        g.fillOval(
                centerX - 5,
                y + 75,
                10,
                10
        );
    }


    @Override
    public String getUnit() {
        return "V";
    }


    @Override
    public String getValueName() {
        return "voltage";
    }


    @Override
    public int getTerminalX(
            int terminal
    ) {

        return x + 40;
    }


    @Override
    public int getTerminalY(
            int terminal
    ) {

        if (terminal == 0) {
            return y;
        }

        return y + 80;
    }
}