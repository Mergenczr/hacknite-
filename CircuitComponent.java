import java.awt.*;

public abstract class CircuitComponent {

    protected int x;
    protected int y;

    protected int width;
    protected int height;

    protected String name;

    protected double value;

    protected boolean selected = false;


    public CircuitComponent(
            int x,
            int y,
            int width,
            int height,
            String name,
            double value
    ) {

        this.x = x;
        this.y = y;

        this.width = width;
        this.height = height;

        this.name = name;
        this.value = value;
    }


    public abstract void draw(Graphics2D g);


    public boolean contains(
            int mouseX,
            int mouseY
    ) {

        return (
                mouseX >= x - 10 &&
                mouseX <= x + width + 10 &&
                mouseY >= y - 25 &&
                mouseY <= y + height + 10
        );
    }


    protected void drawSelection(
            Graphics2D g
    ) {

        if (!selected) {
            return;
        }

        Stroke oldStroke =
                g.getStroke();

        g.setColor(
                new Color(
                        60,
                        120,
                        220
                )
        );

        g.setStroke(
                new BasicStroke(2)
        );

        g.drawRoundRect(
                x - 10,
                y - 20,
                width + 20,
                height + 30,
                10,
                10
        );

        g.setStroke(oldStroke);
    }


    public void setPosition(
            int x,
            int y
    ) {

        this.x = x;
        this.y = y;
    }


    public int getX() {
        return x;
    }


    public int getY() {
        return y;
    }


    public String getName() {
        return name;
    }


    public double getValue() {
        return value;
    }


    public void setValue(
            double value
    ) {

        this.value = value;
    }


    public boolean isSelected() {
        return selected;
    }


    public void setSelected(
            boolean selected
    ) {

        this.selected = selected;
    }


    public abstract String getUnit();


    public abstract String getValueName();


    /*
     * Every component currently has
     * two electrical terminals:
     *
     * terminal 0
     * terminal 1
     */
    public abstract int getTerminalX(
            int terminal
    );


    public abstract int getTerminalY(
            int terminal
    );
}