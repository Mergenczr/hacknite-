import java.awt.*;

public class Wire {

    private Terminal terminal1;
    private Terminal terminal2;

    public Wire(Terminal terminal1, Terminal terminal2) {
        this.terminal1 = terminal1;
        this.terminal2 = terminal2;
    }

    public Terminal getTerminal1() {
        return terminal1;
    }

    public Terminal getTerminal2() {
        return terminal2;
    }

    public void draw(Graphics2D g) {

        g.setColor(Color.BLACK);

        g.drawLine(
                terminal1.getX(),
                terminal1.getY(),
                terminal2.getX(),
                terminal2.getY()
        );
    }

    public void replaceComponent(
            CircuitComponent oldComponent,
            CircuitComponent newComponent
    ) {

        if (terminal1.getComponent() == oldComponent) {

            terminal1 = new Terminal(
                    newComponent,
                    terminal1.getIndex()
            );
        }

        if (terminal2.getComponent() == oldComponent) {

            terminal2 = new Terminal(
                    newComponent,
                    terminal2.getIndex()
            );
        }
    }

    public boolean touches(
            CircuitComponent component
    ) {

        return terminal1.getComponent() == component
                ||
                terminal2.getComponent() == component;
    }
}