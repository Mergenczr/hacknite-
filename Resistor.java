import java.awt.*;

public class Resistor
        extends CircuitComponent {

    public Resistor(
            int x,
            int y,
            String name,
            double resistance
    ) {

        super(
                x,
                y,
                120,
                60,
                name,
                resistance
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
                    " Ω",
                    x + 15,
                    y - 5
            );
        }

        drawSymbol(g);
    }


    public void drawSymbol(
            Graphics2D g
    ) {

        g.setColor(Color.BLACK);

        int centerY =
                y + 30;

        int startX =
                x;

        int endX =
                x + width;

        int resistorStart =
                x + 20;

        int resistorEnd =
                x + width - 20;


        g.drawLine(
                startX,
                centerY,
                resistorStart,
                centerY
        );


        int section =
                (resistorEnd - resistorStart)
                / 8;


        int[] xPoints =
                new int[9];

        int[] yPoints =
                new int[9];


        xPoints[0] =
                resistorStart;

        yPoints[0] =
                centerY;


        for (int i = 1; i < 9; i++) {

            xPoints[i] =
                    resistorStart +
                    i * section;

            if (i == 8) {

                yPoints[i] =
                        centerY;

            } else if (i % 2 == 1) {

                yPoints[i] =
                        centerY - 12;

            } else {

                yPoints[i] =
                        centerY + 12;
            }
        }


        g.drawPolyline(
                xPoints,
                yPoints,
                xPoints.length
        );


        g.drawLine(
                resistorEnd,
                centerY,
                endX,
                centerY
        );


        // Connection terminals
        g.fillOval(
                startX - 5,
                centerY - 5,
                10,
                10
        );

        g.fillOval(
                endX - 5,
                centerY - 5,
                10,
                10
        );
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
    public int getTerminalX(
            int terminal
    ) {

        if (terminal == 0) {
            return x;
        }

        return x + width;
    }


    @Override
    public int getTerminalY(
            int terminal
    ) {

        return y + 30;
    }
}