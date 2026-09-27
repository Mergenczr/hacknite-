public class Terminal {

    private CircuitComponent component;

    private int index;


    public Terminal(
            CircuitComponent component,
            int index
    ) {

        this.component = component;
        this.index = index;
    }


    public CircuitComponent getComponent() {
        return component;
    }


    public int getIndex() {
        return index;
    }


    public int getX() {

        return component.getTerminalX(
                index
        );
    }


    public int getY() {

        return component.getTerminalY(
                index
        );
    }


    public boolean contains(
            int mouseX,
            int mouseY
    ) {

        int dx =
                mouseX - getX();

        int dy =
                mouseY - getY();


        // radius about 10 pixels
        return (
                dx * dx +
                dy * dy
                <= 100
        );
    }
}