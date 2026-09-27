import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("EE Midterm Helper");

            frame.setSize(1100, 700);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            CircuitCanvas canvas = new CircuitCanvas();

            // =================================================
            // TOP TOOLBAR
            // =================================================

            JPanel toolbar = new JPanel();

            JButton transformButton =
                    new JButton("Source Transform");

            JButton deleteButton =
                    new JButton("Delete Selected");

            JButton clearButton =
                    new JButton("Clear Board");


            toolbar.add(transformButton);
            toolbar.add(deleteButton);
            toolbar.add(clearButton);


            transformButton.addActionListener(e ->
                    canvas.sourceTransform()
            );

            deleteButton.addActionListener(e ->
                    canvas.deleteSelected()
            );

            clearButton.addActionListener(e ->
                    canvas.clearBoard()
            );


            frame.setLayout(new BorderLayout());

            frame.add(
                    toolbar,
                    BorderLayout.NORTH
            );

            frame.add(
                    canvas,
                    BorderLayout.CENTER
            );


            frame.setVisible(true);

            canvas.requestFocusInWindow();
        });
    }
}


// =============================================================
// CIRCUIT CANVAS
// =============================================================

class CircuitCanvas extends JPanel
        implements MouseListener,
                   MouseMotionListener,
                   KeyListener {


    private final int PALETTE_WIDTH = 200;


    private final ArrayList<CircuitComponent> components =
            new ArrayList<>();


    private CircuitComponent draggedComponent = null;


    private String draggingNewType = null;


    private int mouseX;
    private int mouseY;


    private int dragOffsetX;
    private int dragOffsetY;


    private int resistorCount = 0;
    private int voltageCount = 0;
    private int currentCount = 0;


    public CircuitCanvas() {

        setBackground(Color.WHITE);

        setFocusable(true);

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
    }


    // =========================================================
    // DRAW EVERYTHING
    // =========================================================

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);


        Graphics2D g2 =
                (Graphics2D) g;


        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );


        drawPalette(g2);


        // Workspace title
        g2.setColor(Color.BLACK);

        g2.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );

        g2.drawString(
                "Circuit Workspace",
                PALETTE_WIDTH + 25,
                35
        );


        // Draw placed components
        for (CircuitComponent component : components) {

            component.draw(g2);
        }


        // Draw transparent preview while dragging
        // something from component palette

        if (draggingNewType != null) {

            Graphics2D preview =
                    (Graphics2D) g2.create();


            preview.setComposite(
                    AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER,
                            0.45f
                    )
            );


            if (draggingNewType.equals("RESISTOR")) {

                new Resistor(
                        mouseX - 60,
                        mouseY - 30,
                        "R?",
                        100
                ).draw(preview);
            }


            else if (
                    draggingNewType.equals("VOLTAGE")
            ) {

                new VoltageSource(
                        mouseX - 40,
                        mouseY - 40,
                        "V?",
                        10
                ).draw(preview);
            }


            else if (
                    draggingNewType.equals("CURRENT")
            ) {

                new CurrentSource(
                        mouseX - 40,
                        mouseY - 40,
                        "I?",
                        1
                ).draw(preview);
            }


            preview.dispose();
        }
    }


    // =========================================================
    // COMPONENT PALETTE
    // =========================================================

    private void drawPalette(Graphics2D g) {

        g.setColor(
                new Color(
                        238,
                        238,
                        238
                )
        );


        g.fillRect(
                0,
                0,
                PALETTE_WIDTH,
                getHeight()
        );


        g.setColor(Color.GRAY);


        g.drawLine(
                PALETTE_WIDTH,
                0,
                PALETTE_WIDTH,
                getHeight()
        );


        g.setColor(Color.BLACK);


        g.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        20
                )
        );


        g.drawString(
                "Components",
                30,
                40
        );


        // =====================================================
        // RESISTOR
        // =====================================================

        Resistor paletteResistor =
                new Resistor(
                        40,
                        70,
                        "",
                        100
                );


        paletteResistor.drawSymbol(g);


        g.drawString(
                "Resistor",
                55,
                145
        );


        // =====================================================
        // VOLTAGE SOURCE
        // =====================================================

        VoltageSource paletteVoltage =
                new VoltageSource(
                        60,
                        175,
                        "",
                        10
                );


        paletteVoltage.drawSymbol(g);


        g.drawString(
                "Voltage Source",
                25,
                270
        );


        // =====================================================
        // CURRENT SOURCE
        // =====================================================

        CurrentSource paletteCurrent =
                new CurrentSource(
                        60,
                        300,
                        "",
                        1
                );


        paletteCurrent.drawSymbol(g);


        g.drawString(
                "Current Source",
                25,
                395
        );


        // =====================================================
        // TRASH
        // =====================================================

        int trashY =
                getHeight() - 110;


        g.setColor(
                new Color(
                        220,
                        220,
                        220
                )
        );


        g.fillRoundRect(
                30,
                trashY,
                140,
                70,
                15,
                15
        );


        g.setColor(Color.DARK_GRAY);


        g.drawRoundRect(
                30,
                trashY,
                140,
                70,
                15,
                15
        );


        g.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        16
                )
        );


        g.drawString(
                "TRASH",
                70,
                trashY + 40
        );
    }


    // =========================================================
    // SOURCE TRANSFORMATION
    // =========================================================

    public void sourceTransform() {


        CircuitComponent source = null;
        Resistor resistor = null;


        int selectedCount = 0;


        for (
                CircuitComponent component :
                components
        ) {

            if (!component.isSelected()) {
                continue;
            }


            selectedCount++;


            if (
                    component instanceof Resistor
            ) {

                resistor =
                        (Resistor) component;
            }


            else if (
                    component instanceof VoltageSource ||
                    component instanceof CurrentSource
            ) {

                source = component;
            }
        }


        // Must have exactly two components selected
        if (
                selectedCount != 2 ||
                source == null ||
                resistor == null
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select exactly one source and one resistor.\n\n" +
                    "Use Shift + Click to select both."
            );

            return;
        }


        double resistance =
                resistor.getValue();


        if (resistance <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Resistance must be greater than 0."
            );

            return;
        }


        // =====================================================
        // VOLTAGE -> CURRENT
        //
        // I = V / R
        // =====================================================

        if (
                source instanceof VoltageSource
        ) {

            double voltage =
                    source.getValue();


            double current =
                    voltage /
                    resistance;


            int x =
                    source.getX();

            int y =
                    source.getY();


            components.remove(source);


            currentCount++;


            CurrentSource newSource =
                    new CurrentSource(
                            x,
                            y,
                            "I" + currentCount,
                            current
                    );


            newSource.setSelected(true);


            components.add(
                    newSource
            );


            JOptionPane.showMessageDialog(
                    this,

                    String.format(
                            "Source Transformation%n%n" +

                            "Voltage source → Current source%n%n" +

                            "I = V / R%n" +

                            "I = %.3f / %.3f%n%n" +

                            "I = %.3f A%n%n" +

                            "The resistor keeps the same value.",
                            voltage,
                            resistance,
                            current
                    )
            );
        }


        // =====================================================
        // CURRENT -> VOLTAGE
        //
        // V = I * R
        // =====================================================

        else if (
                source instanceof CurrentSource
        ) {

            double current =
                    source.getValue();


            double voltage =
                    current *
                    resistance;


            int x =
                    source.getX();

            int y =
                    source.getY();


            components.remove(source);


            voltageCount++;


            VoltageSource newSource =
                    new VoltageSource(
                            x,
                            y,
                            "V" + voltageCount,
                            voltage
                    );


            newSource.setSelected(true);


            components.add(
                    newSource
            );


            JOptionPane.showMessageDialog(
                    this,

                    String.format(
                            "Source Transformation%n%n" +

                            "Current source → Voltage source%n%n" +

                            "V = I × R%n" +

                            "V = %.3f × %.3f%n%n" +

                            "V = %.3f V%n%n" +

                            "The resistor keeps the same value.",
                            current,
                            resistance,
                            voltage
                    )
            );
        }


        repaint();
    }


    // =========================================================
    // DELETE SELECTED
    // =========================================================

    public void deleteSelected() {

        components.removeIf(
                CircuitComponent::isSelected
        );


        repaint();
    }


    // =========================================================
    // CLEAR EVERYTHING
    // =========================================================

    public void clearBoard() {

        if (components.isEmpty()) {
            return;
        }


        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Clear the entire board?",
                        "Clear Board",
                        JOptionPane.YES_NO_OPTION
                );


        if (
                answer ==
                JOptionPane.YES_OPTION
        ) {

            components.clear();

            repaint();
        }
    }


    // =========================================================
    // DESELECT EVERYTHING
    // =========================================================

    private void deselectAll() {

        for (
                CircuitComponent component :
                components
        ) {

            component.setSelected(false);
        }
    }


    // =========================================================
    // COMPONENT HIT TEST
    // =========================================================

    private CircuitComponent componentAt(
            int x,
            int y
    ) {

        // Reverse order because last component drawn
        // should get selected first.

        for (
                int i =
                components.size() - 1;

                i >= 0;

                i--
        ) {

            CircuitComponent component =
                    components.get(i);


            if (
                    component.contains(
                            x,
                            y
                    )
            ) {

                return component;
            }
        }


        return null;
    }


    // =========================================================
    // MOUSE PRESSED
    // =========================================================

    @Override
    public void mousePressed(
            MouseEvent e
    ) {

        requestFocusInWindow();


        mouseX =
                e.getX();

        mouseY =
                e.getY();


        // =====================================================
        // PALETTE
        // =====================================================

        if (
                mouseX <
                PALETTE_WIDTH
        ) {


            // Resistor
            if (
                    mouseY >= 55 &&
                    mouseY <= 155
            ) {

                draggingNewType =
                        "RESISTOR";

                return;
            }


            // Voltage Source
            if (
                    mouseY >= 165 &&
                    mouseY <= 280
            ) {

                draggingNewType =
                        "VOLTAGE";

                return;
            }


            // Current Source
            if (
                    mouseY >= 290 &&
                    mouseY <= 410
            ) {

                draggingNewType =
                        "CURRENT";

                return;
            }


            return;
        }


        // =====================================================
        // WORKSPACE COMPONENT
        // =====================================================

        CircuitComponent component =
                componentAt(
                        mouseX,
                        mouseY
                );


        // Click empty workspace
        if (
                component == null
        ) {

            if (
                    !e.isShiftDown()
            ) {

                deselectAll();
            }


            repaint();

            return;
        }


        // =====================================================
        // RIGHT CLICK DELETE
        // =====================================================

        if (
                SwingUtilities
                        .isRightMouseButton(e)
        ) {

            components.remove(
                    component
            );


            repaint();

            return;
        }


        // =====================================================
        // SELECTION
        // =====================================================

        if (
                e.isShiftDown()
        ) {

            component.setSelected(
                    !component.isSelected()
            );

        } else {

            deselectAll();

            component.setSelected(true);
        }


        draggedComponent =
                component;


        dragOffsetX =
                mouseX -
                component.getX();

        dragOffsetY =
                mouseY -
                component.getY();


        repaint();
    }


    // =========================================================
    // MOUSE DRAG
    // =========================================================

    @Override
    public void mouseDragged(
            MouseEvent e
    ) {

        mouseX =
                e.getX();

        mouseY =
                e.getY();


        if (
                draggingNewType != null
        ) {

            repaint();

            return;
        }


        if (
                draggedComponent != null
        ) {

            int newX =
                    mouseX -
                    dragOffsetX;

            int newY =
                    mouseY -
                    dragOffsetY;


            draggedComponent.setPosition(
                    newX,
                    newY
            );


            repaint();
        }
    }


    // =========================================================
    // MOUSE RELEASE
    // =========================================================

    @Override
    public void mouseReleased(
            MouseEvent e
    ) {

        mouseX =
                e.getX();

        mouseY =
                e.getY();


        // =====================================================
        // DROP NEW COMPONENT
        // =====================================================

        if (
                draggingNewType != null
        ) {

            if (
                    mouseX >
                    PALETTE_WIDTH
            ) {


                if (
                        draggingNewType.equals(
                                "RESISTOR"
                        )
                ) {

                    resistorCount++;


                    components.add(
                            new Resistor(
                                    mouseX - 60,
                                    mouseY - 30,
                                    "R" + resistorCount,
                                    100
                            )
                    );
                }


                else if (
                        draggingNewType.equals(
                                "VOLTAGE"
                        )
                ) {

                    voltageCount++;


                    components.add(
                            new VoltageSource(
                                    mouseX - 40,
                                    mouseY - 40,
                                    "V" + voltageCount,
                                    10
                            )
                    );
                }


                else if (
                        draggingNewType.equals(
                                "CURRENT"
                        )
                ) {

                    currentCount++;


                    components.add(
                            new CurrentSource(
                                    mouseX - 40,
                                    mouseY - 40,
                                    "I" + currentCount,
                                    1
                            )
                    );
                }
            }


            draggingNewType =
                    null;


            repaint();

            return;
        }


        // =====================================================
        // DRAG COMPONENT INTO TRASH
        // =====================================================

        if (
                draggedComponent != null
        ) {


            int trashY =
                    getHeight() - 110;


            if (
                    mouseX >= 30 &&
                    mouseX <= 170 &&
                    mouseY >= trashY &&
                    mouseY <= trashY + 70
            ) {

                components.remove(
                        draggedComponent
                );
            }


            // If component was accidentally dragged
            // into palette but not trash,
            // move it back into workspace.

            else if (
                    draggedComponent.getX() <
                    PALETTE_WIDTH + 10
            ) {

                draggedComponent.setPosition(
                        PALETTE_WIDTH + 20,
                        draggedComponent.getY()
                );
            }
        }


        draggedComponent =
                null;


        repaint();
    }


    // =========================================================
    // DOUBLE CLICK EDIT VALUE
    // =========================================================

    @Override
    public void mouseClicked(
            MouseEvent e
    ) {

        if (
                e.getClickCount() != 2 ||
                !SwingUtilities
                        .isLeftMouseButton(e)
        ) {

            return;
        }


        CircuitComponent component =
                componentAt(
                        e.getX(),
                        e.getY()
                );


        if (
                component == null
        ) {

            return;
        }


        String unit =
                component.getUnit();


        String input =
                JOptionPane.showInputDialog(
                        this,

                        "Enter " +
                        component.getValueName() +
                        " (" +
                        unit +
                        "):",

                        component.getValue()
                );


        if (
                input == null
        ) {

            return;
        }


        try {

            double value =
                    Double.parseDouble(
                            input
                    );


            if (
                    component instanceof Resistor &&
                    value <= 0
            ) {

                JOptionPane.showMessageDialog(
                        this,
                        "Resistance must be greater than 0."
                );

                return;
            }


            component.setValue(
                    value
            );


            repaint();

        }

        catch (
                NumberFormatException ex
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter a valid number."
            );
        }
    }


    // =========================================================
    // KEYBOARD DELETE
    // =========================================================

    @Override
    public void keyPressed(
            KeyEvent e
    ) {

        if (
                e.getKeyCode() ==
                KeyEvent.VK_DELETE ||

                e.getKeyCode() ==
                KeyEvent.VK_BACK_SPACE
        ) {

            deleteSelected();
        }
    }


    @Override
    public void keyTyped(
            KeyEvent e
    ) {
    }


    @Override
    public void keyReleased(
            KeyEvent e
    ) {
    }


    @Override
    public void mouseMoved(
            MouseEvent e
    ) {
    }


    @Override
    public void mouseEntered(
            MouseEvent e
    ) {
    }


    @Override
    public void mouseExited(
            MouseEvent e
    ) {
    }
}


// =============================================================
// BASE COMPONENT
// =============================================================

abstract class CircuitComponent {


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


    public abstract void draw(
            Graphics2D g
    );


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

        if (
                !selected
        ) {

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


        g.setStroke(
                oldStroke
        );
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
}


// =============================================================
// RESISTOR
// =============================================================

class Resistor
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


        if (
                !name.isEmpty()
        ) {

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


        int[] xp =
                new int[9];

        int[] yp =
                new int[9];


        xp[0] =
                resistorStart;

        yp[0] =
                centerY;


        for (
                int i = 1;
                i < 9;
                i++
        ) {

            xp[i] =
                    resistorStart +
                    i * section;


            if (
                    i == 8
            ) {

                yp[i] =
                        centerY;
            }

            else if (
                    i % 2 == 1
            ) {

                yp[i] =
                        centerY - 12;
            }

            else {

                yp[i] =
                        centerY + 12;
            }
        }


        g.drawPolyline(
                xp,
                yp,
                xp.length
        );


        g.drawLine(
                resistorEnd,
                centerY,
                endX,
                centerY
        );


        // terminals
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
}


// =============================================================
// VOLTAGE SOURCE
// =============================================================

class VoltageSource
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


        if (
                !name.isEmpty()
        ) {

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


        // terminals / wires
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
                "−",
                centerX - 5,
                centerY + 18
        );


        // terminals
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
}


// =============================================================
// CURRENT SOURCE
// =============================================================

class CurrentSource
        extends CircuitComponent {


    public CurrentSource(
            int x,
            int y,
            String name,
            double current
    ) {

        super(
                x,
                y,
                80,
                80,
                name,
                current
        );
    }


    @Override
    public void draw(
            Graphics2D g
    ) {

        drawSelection(g);


        g.setColor(Color.BLACK);


        if (
                !name.isEmpty()
        ) {

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
                    " A",

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


        // wires
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


        // source circle
        g.drawOval(
                centerX - 25,
                centerY - 25,
                50,
                50
        );


        // arrow
        g.drawLine(
                centerX,
                centerY + 14,
                centerX,
                centerY - 14
        );


        g.drawLine(
                centerX,
                centerY - 14,
                centerX - 6,
                centerY - 5
        );


        g.drawLine(
                centerX,
                centerY - 14,
                centerX + 6,
                centerY - 5
        );


        // terminals
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

        return "A";
    }


    @Override
    public String getValueName() {

        return "current";
    }
}