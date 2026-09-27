import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class CircuitCanvas
        extends JPanel
        implements MouseListener,
                   MouseMotionListener,
                   KeyListener {

    private final int PALETTE_WIDTH =
            200;


    private final ArrayList<CircuitComponent>
            components =
            new ArrayList<>();


    private final ArrayList<Wire>
            wires =
            new ArrayList<>();


    private CircuitComponent
            draggedComponent = null;


    private Terminal
            selectedTerminal = null;


    private String
            draggingNewType = null;


    private int mouseX;

    private int mouseY;


    private int dragOffsetX;

    private int dragOffsetY;


    private int resistorCount = 0;

    private int voltageCount = 0;

    private int currentCount = 0;

    private int wireCount = 0;


    public CircuitCanvas() {

        setBackground(
                Color.WHITE
        );

        setFocusable(true);

        addMouseListener(this);

        addMouseMotionListener(this);

        addKeyListener(this);

    }


    // =========================================================
    // DRAW
    // =========================================================

    @Override
    protected void paintComponent(
            Graphics g
    ) {

        super.paintComponent(g);


        Graphics2D g2 =
                (Graphics2D) g;


        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );


        drawPalette(g2);


        g2.setColor(
                Color.BLACK
        );


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


        // Draw wires first
        for (
                Wire wire :
                wires
        ) {

            wire.draw(g2);
        }


        // Draw components over wires
        for (
                CircuitComponent component :
                components
        ) {

            component.draw(g2);
        }


        // Highlight terminal waiting for connection
        if (
                selectedTerminal != null
        ) {

            g2.setColor(
                    Color.RED
            );


            g2.fillOval(
                    selectedTerminal.getX() - 7,
                    selectedTerminal.getY() - 7,
                    14,
                    14
            );
        }


        // Preview while dragging new component
        if (
                draggingNewType != null
        ) {

            Graphics2D preview =
                    (Graphics2D) g2.create();


            preview.setComposite(
                    AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER,
                            0.45f
                    )
            );


            if (
                    draggingNewType.equals(
                            "RESISTOR"
                    )
            ) {

                new Resistor(
                        mouseX - 60,
                        mouseY - 30,
                        "R?",
                        100
                ).draw(preview);
            }


            else if (
                    draggingNewType.equals(
                            "RESISTOR_Y"
                    )
            ) {

                Resistor previewResistor =
                        new Resistor(
                                mouseX - 30,
                                mouseY - 60,
                                "R?",
                                100
                        );

                previewResistor.rotate();
                previewResistor.draw(preview);
            }


            else if (
                    draggingNewType.equals(
                            "JUSTWIRE"
                    )
            ) {

                new JustWire(
                        mouseX - 60,
                        mouseY - 20,
                        "W?"
                ).draw(preview);
            }


            else if (
                    draggingNewType.equals(
                            "VOLTAGE"
                    )
            ) {

                new VoltageSource(
                        mouseX - 40,
                        mouseY - 40,
                        "V?",
                        10
                ).draw(preview);
            }


            else if (
                    draggingNewType.equals(
                            "CURRENT"
                    )
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
    // PALETTE
    // =========================================================

    private void drawPalette(
            Graphics2D g
    ) {

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


        g.setColor(
                Color.GRAY
        );


        g.drawLine(
                PALETTE_WIDTH,
                0,
                PALETTE_WIDTH,
                getHeight()
        );


        g.setColor(
                Color.BLACK
        );


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


        // Horizontal resistor (X direction)
        Resistor resistorX =
                new Resistor(
                        10,
                        70,
                        "",
                        100
                );

        resistorX.drawSymbol(g);

        g.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        g.drawString(
                "R-X",
                50,
                145
        );


        // Vertical resistor (Y direction)
        Resistor resistorY =
                new Resistor(
                        135,
                        60,
                        "",
                        100
                );

        resistorY.rotate();
        resistorY.drawSymbol(g);

        g.drawString(
                "R-Y",
                150,
                195
        );


        VoltageSource voltage =
                new VoltageSource(
                        60,
                        215,
                        "",
                        10
                );

        voltage.drawSymbol(g);

        g.drawString(
                "Voltage Source",
                35,
                310
        );


        CurrentSource current =
                new CurrentSource(
                        60,
                        335,
                        "",
                        1
                );

        current.drawSymbol(g);

        g.drawString(
                "Current Source",
                35,
                430
        );


        JustWire justWire =
                new JustWire(
                        40,
                        465,
                        ""
                );

        justWire.drawSymbol(g);

        g.drawString(
                "Wire",
                80,
                530
        );


        // Trash
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


        g.setColor(
                Color.DARK_GRAY
        );


        g.drawRoundRect(
                30,
                trashY,
                140,
                70,
                15,
                15
        );


        g.drawString(
                "TRASH",
                70,
                trashY + 40
        );
    }


    // =========================================================
    // FIND TERMINAL
    // =========================================================

    private Terminal findTerminal(
            int x,
            int y
    ) {

        for (
                CircuitComponent component :
                components
        ) {

            Terminal t0 =
                    new Terminal(
                            component,
                            0
                    );


            Terminal t1 =
                    new Terminal(
                            component,
                            1
                    );


            if (
                    t0.contains(
                            x,
                            y
                    )
            ) {

                return t0;
            }


            if (
                    t1.contains(
                            x,
                            y
                    )
            ) {

                return t1;
            }
        }


        return null;
    }


    // =========================================================
    // FIND COMPONENT
    // =========================================================

    private CircuitComponent componentAt(
            int x,
            int y
    ) {

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
    // DESELECT
    // =========================================================

    private void deselectAll() {

        for (
                CircuitComponent component :
                components
        ) {

            component.setSelected(
                    false
            );
        }
    }


    // =========================================================
    // DELETE WIRES ATTACHED TO COMPONENT
    // =========================================================

    private void removeWiresFor(
            CircuitComponent component
    ) {

        wires.removeIf(
                wire ->
                wire.touches(component)
        );
    }


    // =========================================================
    // DELETE SELECTED
    // =========================================================

    public void deleteSelected() {

        ArrayList<CircuitComponent>
                removeList =
                new ArrayList<>();


        for (
                CircuitComponent component :
                components
        ) {

            if (
                    component.isSelected()
            ) {

                removeList.add(
                        component
                );
            }
        }


        for (
                CircuitComponent component :
                removeList
        ) {

            removeWiresFor(
                    component
            );

            components.remove(
                    component
            );
        }


        selectedTerminal =
                null;


        repaint();
    }


    // =========================================================
    // CLEAR
    // =========================================================

    public void clearBoard() {

        if (
                components.isEmpty()
        ) {

            return;
        }


        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        "Clear the entire board?",
                        "Clear Board",
                        JOptionPane.YES_NO_OPTION
                );


        if (
                result ==
                JOptionPane.YES_OPTION
        ) {

            components.clear();

            wires.clear();

            selectedTerminal =
                    null;

            repaint();
        }
    }


    // =========================================================
    // SOURCE TRANSFORMATION
    // =========================================================

    public void sourceTransform() {

        CircuitComponent source = null;
        Resistor resistor = null;
        int selectedCount = 0;


        for (CircuitComponent component : components) {

            if (!component.isSelected()) {
                continue;
            }

            selectedCount++;

            if (component instanceof Resistor) {

                resistor =
                        (Resistor) component;

            } else if (
                    component instanceof VoltageSource
                    ||
                    component instanceof CurrentSource
            ) {

                source =
                        component;
            }
        }


        if (
                selectedCount != 2
                ||
                source == null
                ||
                resistor == null
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select exactly one source and one resistor.\n"
                    +
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


        CircuitSolver.NodeModel model =
                CircuitSolver.buildNodeModel(
                        components,
                        wires
                );


        // =====================================================
        // THEVENIN -> NORTON
        //
        // Voltage source + series resistor
        // becomes
        // Current source || resistor
        // =====================================================

        if (source instanceof VoltageSource) {

            int source0 =
                    model.getNode(
                            source,
                            0
                    );

            int source1 =
                    model.getNode(
                            source,
                            1
                    );

            int resistor0 =
                    model.getNode(
                            resistor,
                            0
                    );

            int resistor1 =
                    model.getNode(
                            resistor,
                            1
                    );


            int sharedNode = -1;


            if (
                    source0 == resistor0
                    ||
                    source0 == resistor1
            ) {

                sharedNode =
                        source0;
            }


            if (
                    source1 == resistor0
                    ||
                    source1 == resistor1
            ) {

                if (sharedNode != -1) {

                    JOptionPane.showMessageDialog(
                            this,
                            "The selected voltage source and resistor "
                            +
                            "are parallel, not series."
                    );

                    return;
                }

                sharedNode =
                        source1;
            }


            if (
                    sharedNode == -1
                    ||
                    model.countRealComponentTerminals(
                            sharedNode
                    ) != 2
            ) {

                JOptionPane.showMessageDialog(
                        this,
                        "For V -> I transformation, the selected "
                        +
                        "voltage source and resistor must be in series."
                );

                return;
            }


            int sourceOuterTerminal =
                    source0 == sharedNode
                    ?
                    1
                    :
                    0;


            int resistorOuterTerminal =
                    resistor0 == sharedNode
                    ?
                    1
                    :
                    0;


            int sourceOuterNode =
                    model.getNode(
                            source,
                            sourceOuterTerminal
                    );


            int resistorOuterNode =
                    model.getNode(
                            resistor,
                            resistorOuterTerminal
                    );


            ArrayList<Terminal> sourceSide =
                    model.getExternalTerminals(
                            sourceOuterNode,
                            source,
                            resistor
                    );


            ArrayList<Terminal> resistorSide =
                    model.getExternalTerminals(
                            resistorOuterNode,
                            source,
                            resistor
                    );


            double voltage =
                    source.getValue();


            double current =
                    CircuitSolver.voltageToCurrent(
                            voltage,
                            resistance
                    );


            int sourceX =
                    source.getX();

            int sourceY =
                    source.getY();


            removeWiresFor(source);
            removeWiresFor(resistor);

            components.remove(source);


            currentCount++;


            CurrentSource newSource =
                    new CurrentSource(
                            sourceX,
                            sourceY,
                            "I" + currentCount,
                            current
                    );


            /*
             * Make the resistor vertical so the new Norton
             * equivalent is visually parallel.
             */
            if (!resistor.isVertical()) {
                resistor.rotate();
            }


            resistor.setPosition(
                    sourceX + 120,
                    sourceY - 20
            );


            /*
             * Norton polarity:
             * CurrentSource terminal 0 is the arrow-head node.
             *
             * If the original voltage source's positive terminal
             * was the outer source-side terminal, terminal 0 goes
             * to that node. Otherwise the polarity is reversed.
             */
            boolean sourceOuterWasPositive =
                    sourceOuterTerminal == 0;


            Terminal currentSourceSide =
                    new Terminal(
                            newSource,
                            sourceOuterWasPositive
                            ?
                            0
                            :
                            1
                    );


            Terminal currentResistorSide =
                    new Terminal(
                            newSource,
                            sourceOuterWasPositive
                            ?
                            1
                            :
                            0
                    );


            Terminal resistorSourceSide =
                    new Terminal(
                            resistor,
                            0
                    );


            Terminal resistorResistorSide =
                    new Terminal(
                            resistor,
                            1
                    );


            // Put current source and resistor in parallel.
            wires.add(
                    new Wire(
                            currentSourceSide,
                            resistorSourceSide
                    )
            );

            wires.add(
                    new Wire(
                            currentResistorSide,
                            resistorResistorSide
                    )
            );


            // Restore everything that was connected to the
            // two external Thevenin terminals.
            for (Terminal terminal : sourceSide) {

                wires.add(
                        new Wire(
                                currentSourceSide,
                                new Terminal(
                                        terminal.getComponent(),
                                        terminal.getIndex()
                                )
                        )
                );
            }


            for (Terminal terminal : resistorSide) {

                wires.add(
                        new Wire(
                                currentResistorSide,
                                new Terminal(
                                        terminal.getComponent(),
                                        terminal.getIndex()
                                )
                        )
                );
            }


            components.add(
                    newSource
            );


            deselectAll();

            newSource.setSelected(true);
            resistor.setSelected(true);


            JOptionPane.showMessageDialog(
                    this,
                    String.format(
                            "Voltage source + series resistor -> "
                            +
                            "Current source || resistor%n%n"
                            +
                            "I = V / R%n"
                            +
                            "I = %.3f / %.3f%n"
                            +
                            "I = %.3f A",
                            voltage,
                            resistance,
                            current
                    )
            );


            repaint();

            return;
        }


        // =====================================================
        // NORTON -> THEVENIN
        //
        // Current source || resistor
        // becomes
        // Voltage source + series resistor
        // =====================================================

        CurrentSource currentSource =
                (CurrentSource) source;


        int source0 =
                model.getNode(
                        currentSource,
                        0
                );

        int source1 =
                model.getNode(
                        currentSource,
                        1
                );

        int resistor0 =
                model.getNode(
                        resistor,
                        0
                );

        int resistor1 =
                model.getNode(
                        resistor,
                        1
                );


        boolean parallel =
                (
                        source0 == resistor0
                        &&
                        source1 == resistor1
                )
                ||
                (
                        source0 == resistor1
                        &&
                        source1 == resistor0
                );


        if (!parallel) {

            JOptionPane.showMessageDialog(
                    this,
                    "For I -> V transformation, the selected "
                    +
                    "current source and resistor must be parallel."
            );

            return;
        }


        ArrayList<Terminal> positiveSide =
                model.getExternalTerminals(
                        source0,
                        currentSource,
                        resistor
                );


        ArrayList<Terminal> negativeSide =
                model.getExternalTerminals(
                        source1,
                        currentSource,
                        resistor
                );


        double current =
                currentSource.getValue();


        double voltage =
                CircuitSolver.currentToVoltage(
                        current,
                        resistance
                );


        int sourceX =
                currentSource.getX();

        int sourceY =
                currentSource.getY();


        removeWiresFor(currentSource);
        removeWiresFor(resistor);

        components.remove(
                currentSource
        );


        voltageCount++;


        VoltageSource newSource =
                new VoltageSource(
                        sourceX,
                        sourceY,
                        "V" + voltageCount,
                        voltage
                );


        /*
         * Build a vertical series pair:
         *
         * positive outer node
         *       |
         *      V
         *       |
         *      R
         *       |
         * negative outer node
         */
        if (!resistor.isVertical()) {
            resistor.rotate();
        }


        resistor.setPosition(
                sourceX + 10,
                sourceY + 110
        );


        Terminal voltagePositive =
                new Terminal(
                        newSource,
                        0
                );


        Terminal voltageNegative =
                new Terminal(
                        newSource,
                        1
                );


        Terminal resistorTop =
                new Terminal(
                        resistor,
                        0
                );


        Terminal resistorBottom =
                new Terminal(
                        resistor,
                        1
                );


        // Internal series connection.
        wires.add(
                new Wire(
                        voltageNegative,
                        resistorTop
                )
        );


        // Restore original positive Norton node.
        for (Terminal terminal : positiveSide) {

            wires.add(
                    new Wire(
                            voltagePositive,
                            new Terminal(
                                    terminal.getComponent(),
                                    terminal.getIndex()
                            )
                    )
            );
        }


        // Restore original negative Norton node.
        for (Terminal terminal : negativeSide) {

            wires.add(
                    new Wire(
                            resistorBottom,
                            new Terminal(
                                    terminal.getComponent(),
                                    terminal.getIndex()
                            )
                    )
            );
        }


        components.add(
                newSource
        );


        deselectAll();

        newSource.setSelected(true);
        resistor.setSelected(true);


        JOptionPane.showMessageDialog(
                this,
                String.format(
                        "Current source || resistor -> "
                        +
                        "Voltage source + series resistor%n%n"
                        +
                        "V = I x R%n"
                        +
                        "V = %.3f x %.3f%n"
                        +
                        "V = %.3f V",
                        current,
                        resistance,
                        voltage
                )
        );


        repaint();
    }


    // =========================================================
    // SOLVER
    // =========================================================

    public void solveCircuit() {

        String result =
                CircuitSolver.solve(
                        components,
                        wires
                );


        JTextArea output =
                new JTextArea(
                        result,
                        20,
                        45
                );


        output.setEditable(
                false
        );


        output.setFont(
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        14
                )
        );


        JOptionPane.showMessageDialog(
                this,
                new JScrollPane(output),
                "Circuit Results",
                JOptionPane.INFORMATION_MESSAGE
        );
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


        // Palette
        if (
                mouseX <
                PALETTE_WIDTH
        ) {

            // Horizontal resistor: left side of the resistor row
            if (
                    mouseX < 130
                    &&
                    mouseY >= 55
                    &&
                    mouseY <= 155
            ) {

                draggingNewType =
                        "RESISTOR";

                return;
            }


            // Vertical resistor: right side of the resistor row
            if (
                    mouseX >= 130
                    &&
                    mouseY >= 55
                    &&
                    mouseY <= 205
            ) {

                draggingNewType =
                        "RESISTOR_Y";

                return;
            }


            if (
                    mouseY >= 205
                    &&
                    mouseY <= 325
            ) {

                draggingNewType =
                        "VOLTAGE";

                return;
            }


            if (
                    mouseY >= 325
                    &&
                    mouseY <= 445
            ) {

                draggingNewType =
                        "CURRENT";

                return;
            }


            if (
                    mouseY >= 450
                    &&
                    mouseY <= 540
            ) {

                draggingNewType =
                        "JUSTWIRE";

                return;
            }


            return;
        }


        /*
         * Check terminals BEFORE
         * checking component body.
         */
        Terminal terminal =
                findTerminal(
                        mouseX,
                        mouseY
                );


        if (
                terminal != null
        ) {

            if (
                    selectedTerminal == null
            ) {

                selectedTerminal =
                        terminal;
            }

            else {

                if (
                        selectedTerminal
                        .getComponent()
                        !=
                        terminal
                        .getComponent()
                ) {

                    wires.add(
                            new Wire(
                                    selectedTerminal,
                                    terminal
                            )
                    );
                }


                selectedTerminal =
                        null;
            }


            repaint();

            return;
        }


        CircuitComponent component =
                componentAt(
                        mouseX,
                        mouseY
                );


        if (
                component == null
        ) {

            if (
                    !e.isShiftDown()
            ) {

                deselectAll();
            }


            selectedTerminal =
                    null;


            repaint();

            return;
        }


        // Right click delete
        if (
                SwingUtilities
                .isRightMouseButton(e)
        ) {

            removeWiresFor(
                    component
            );


            components.remove(
                    component
            );


            repaint();

            return;
        }


        if (
                e.isShiftDown()
        ) {

            component.setSelected(
                    !component.isSelected()
            );
        }

        else {

            deselectAll();

            component.setSelected(
                    true
            );
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
    // MOUSE DRAGGED
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

            draggedComponent
                    .setPosition(

                    mouseX -
                    dragOffsetX,

                    mouseY -
                    dragOffsetY
            );


            repaint();
        }
    }


    // =========================================================
    // MOUSE RELEASED
    // =========================================================

    @Override
    public void mouseReleased(
            MouseEvent e
    ) {

        mouseX =
                e.getX();

        mouseY =
                e.getY();


        // Drop new component
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
                                "RESISTOR_Y"
                        )
                ) {

                    resistorCount++;

                    Resistor verticalResistor =
                            new Resistor(
                                    mouseX - 30,
                                    mouseY - 60,
                                    "R" + resistorCount,
                                    100
                            );

                    verticalResistor.rotate();

                    components.add(
                            verticalResistor
                    );
                }


                else if (
                        draggingNewType.equals(
                                "JUSTWIRE"
                        )
                ) {

                    wireCount++;

                    components.add(
                            new JustWire(
                                    mouseX - 60,
                                    mouseY - 20,
                                    "W" + wireCount
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


        // Trash
        if (
                draggedComponent != null
        ) {

            int trashY =
                    getHeight() - 110;


            if (
                    mouseX >= 30
                    &&
                    mouseX <= 170
                    &&
                    mouseY >= trashY
                    &&
                    mouseY <= trashY + 70
            ) {

                removeWiresFor(
                        draggedComponent
                );


                components.remove(
                        draggedComponent
                );
            }


            else if (
                    draggedComponent.getX()
                    <
                    PALETTE_WIDTH + 10
            ) {

                draggedComponent
                        .setPosition(
                                PALETTE_WIDTH + 20,
                                draggedComponent
                                .getY()
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
                e.getClickCount() != 2
                ||
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


        String input =
                JOptionPane.showInputDialog(
                        this,

                        "Enter "
                        +
                        component.getValueName()
                        +
                        " ("
                        +
                        component.getUnit()
                        +
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
                    component
                    instanceof Resistor
                    &&
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
    // ROTATE SELECTED
    // =========================================================

    public void rotateSelected() {

        boolean rotatedSomething = false;

        for (
                CircuitComponent component :
                components
        ) {

            if (!component.isSelected()) {
                continue;
            }

            component.rotate();
            rotatedSomething = true;
        }

        if (rotatedSomething) {
            revalidate();
            repaint();
        }

        requestFocusInWindow();
    }


    // =========================================================
    // KEYBOARD
    // =========================================================

    @Override
    public void keyPressed(
            KeyEvent e
    ) {

        if (
                e.getKeyCode() == KeyEvent.VK_DELETE
                ||
                e.getKeyCode() == KeyEvent.VK_BACK_SPACE
        ) {

            deleteSelected();
            return;
        }


        if (
                e.getKeyCode() == KeyEvent.VK_ESCAPE
        ) {

            selectedTerminal = null;
            draggingNewType = null;
            repaint();
            return;
        }


        if (
                e.getKeyCode() == KeyEvent.VK_R
        ) {

            rotateSelected();
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