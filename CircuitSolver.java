import java.util.*;

public class CircuitSolver {

    // =========================================================
    // BASIC SOURCE TRANSFORMATION MATH
    // =========================================================

    public static double voltageToCurrent(
            double voltage,
            double resistance
    ) {
        if (resistance == 0) {
            throw new IllegalArgumentException(
                    "Resistance cannot be zero."
            );
        }

        return voltage / resistance;
    }


    public static double currentToVoltage(
            double current,
            double resistance
    ) {
        return current * resistance;
    }


    // =========================================================
    // TERMINAL KEY
    //
    // We identify a terminal by:
    // component object + terminal number.
    // =========================================================

    private static class TerminalKey {

        CircuitComponent component;
        int terminal;

        TerminalKey(
                CircuitComponent component,
                int terminal
        ) {
            this.component = component;
            this.terminal = terminal;
        }

        @Override
        public boolean equals(Object object) {

            if (!(object instanceof TerminalKey)) {
                return false;
            }

            TerminalKey other =
                    (TerminalKey) object;

            return component == other.component
                    &&
                    terminal == other.terminal;
        }

        @Override
        public int hashCode() {

            return System.identityHashCode(component) * 31
                    + terminal;
        }
    }


    // =========================================================
    // UNION FIND
    //
    // Used to detect which terminals belong
    // to the same electrical node.
    // =========================================================

    private static class UnionFind {

        int[] parent;

        UnionFind(int size) {

            parent = new int[size];

            for (int i = 0; i < size; i++) {
                parent[i] = i;
            }
        }

        int find(int value) {

            if (parent[value] != value) {
                parent[value] =
                        find(parent[value]);
            }

            return parent[value];
        }

        void union(int a, int b) {

            int rootA = find(a);
            int rootB = find(b);

            if (rootA != rootB) {
                parent[rootB] = rootA;
            }
        }
    }


    // =========================================================
    // NODE MODEL
    // =========================================================

    private static class NodeModel {

        Map<TerminalKey, Integer> nodeMap;
        int nodeCount;

        NodeModel(
                Map<TerminalKey, Integer> nodeMap,
                int nodeCount
        ) {
            this.nodeMap = nodeMap;
            this.nodeCount = nodeCount;
        }

        int getNode(
                CircuitComponent component,
                int terminal
        ) {

            Integer node =
                    nodeMap.get(
                            new TerminalKey(
                                    component,
                                    terminal
                            )
                    );

            if (node == null) {
                return -1;
            }

            return node;
        }
    }


    // =========================================================
    // RESISTOR EDGE
    // =========================================================

    private static class ResistorEdge {

        Resistor resistor;
        int nodeA;
        int nodeB;

        ResistorEdge(
                Resistor resistor,
                int nodeA,
                int nodeB
        ) {
            this.resistor = resistor;
            this.nodeA = nodeA;
            this.nodeB = nodeB;
        }

        int otherNode(int node) {

            if (node == nodeA) {
                return nodeB;
            }

            return nodeA;
        }
    }


    // =========================================================
    // BUILD ELECTRICAL NODES
    // =========================================================

    private static NodeModel buildNodeModel(
            ArrayList<CircuitComponent> components,
            ArrayList<Wire> wires
    ) {

        ArrayList<TerminalKey> terminals =
                new ArrayList<>();

        Map<TerminalKey, Integer> terminalIndex =
                new HashMap<>();


        for (CircuitComponent component : components) {

            for (int terminal = 0; terminal < 2; terminal++) {

                TerminalKey key =
                        new TerminalKey(
                                component,
                                terminal
                        );

                terminalIndex.put(
                        key,
                        terminals.size()
                );

                terminals.add(key);
            }
        }


        UnionFind unionFind =
                new UnionFind(
                        terminals.size()
                );


        // Normal drawn connections
        for (Wire wire : wires) {

            Terminal first =
                    wire.getTerminal1();

            Terminal second =
                    wire.getTerminal2();


            Integer firstIndex =
                    terminalIndex.get(
                            new TerminalKey(
                                    first.getComponent(),
                                    first.getIndex()
                            )
                    );

            Integer secondIndex =
                    terminalIndex.get(
                            new TerminalKey(
                                    second.getComponent(),
                                    second.getIndex()
                            )
                    );


            if (
                    firstIndex != null
                    &&
                    secondIndex != null
            ) {

                unionFind.union(
                        firstIndex,
                        secondIndex
                );
            }
        }


        // A JustWire is an ideal conductor,
        // so both of its ends are the same node.
        for (CircuitComponent component : components) {

            if (component instanceof JustWire) {

                Integer firstIndex =
                        terminalIndex.get(
                                new TerminalKey(
                                        component,
                                        0
                                )
                        );

                Integer secondIndex =
                        terminalIndex.get(
                                new TerminalKey(
                                        component,
                                        1
                                )
                        );


                if (
                        firstIndex != null
                        &&
                        secondIndex != null
                ) {

                    unionFind.union(
                            firstIndex,
                            secondIndex
                    );
                }
            }
        }


        Map<Integer, Integer> rootToNode =
                new HashMap<>();

        Map<TerminalKey, Integer> nodeMap =
                new HashMap<>();

        int nodeCounter = 0;


        for (int i = 0; i < terminals.size(); i++) {

            int root =
                    unionFind.find(i);


            if (!rootToNode.containsKey(root)) {

                rootToNode.put(
                        root,
                        nodeCounter
                );

                nodeCounter++;
            }


            nodeMap.put(
                    terminals.get(i),
                    rootToNode.get(root)
            );
        }


        return new NodeModel(
                nodeMap,
                nodeCounter
        );
    }


    // =========================================================
    // MAIN SOLVER
    // =========================================================

    public static String solve(
            ArrayList<CircuitComponent> components,
            ArrayList<Wire> wires
    ) {

        StringBuilder output =
                new StringBuilder();


        output.append("CIRCUIT ANALYSIS\n");
        output.append("========================================\n\n");


        if (components.isEmpty()) {

            output.append(
                    "The board is empty.\n"
            );

            return output.toString();
        }


        NodeModel nodes =
                buildNodeModel(
                        components,
                        wires
                );


        ArrayList<Resistor> resistors =
                new ArrayList<>();

        ArrayList<VoltageSource> voltageSources =
                new ArrayList<>();

        ArrayList<CurrentSource> currentSources =
                new ArrayList<>();


        for (CircuitComponent component : components) {

            if (component instanceof Resistor) {

                resistors.add(
                        (Resistor) component
                );

            } else if (
                    component instanceof VoltageSource
            ) {

                voltageSources.add(
                        (VoltageSource) component
                );

            } else if (
                    component instanceof CurrentSource
            ) {

                currentSources.add(
                        (CurrentSource) component
                );
            }
        }


        output.append(
                "Electrical nodes detected: "
                + nodes.nodeCount
                + "\n"
        );

        output.append(
                "Resistors: "
                + resistors.size()
                + "\n\n"
        );


        if (resistors.isEmpty()) {

            output.append(
                    "No resistors are available to calculate Req.\n"
            );

            return output.toString();
        }


        // Build resistor graph
        ArrayList<ResistorEdge> edges =
                new ArrayList<>();


        for (Resistor resistor : resistors) {

            if (resistor.getValue() <= 0) {

                output.append(
                        "ERROR: "
                        + resistor.getName()
                        + " must be greater than 0 ohms.\n"
                );

                return output.toString();
            }


            int nodeA =
                    nodes.getNode(
                            resistor,
                            0
                    );

            int nodeB =
                    nodes.getNode(
                            resistor,
                            1
                    );


            edges.add(
                    new ResistorEdge(
                            resistor,
                            nodeA,
                            nodeB
                    )
            );
        }


        // Show obvious parallel groups first.
        appendParallelGroups(
                output,
                edges
        );


        // For now, current sources are not included
        // in the voltage-source Req/I mode.
        if (!currentSources.isEmpty()) {

            output.append(
                    "NOTE: A current source is present.\n"
            );

            output.append(
                    "The automatic Req + I calculation below "
                    + "currently handles a resistor network driven "
                    + "by one voltage source.\n\n"
            );
        }


        if (voltageSources.isEmpty()) {

            output.append(
                    "No voltage source found.\n"
            );

            output.append(
                    "Add a voltage source if you want the program "
                    + "to calculate I = V / Req automatically.\n"
            );

            return output.toString();
        }


        if (voltageSources.size() > 1) {

            output.append(
                    "More than one voltage source is present.\n"
            );

            output.append(
                    "For now, Req + I mode supports one voltage source "
                    + "at a time.\n"
            );

            return output.toString();
        }


        if (!currentSources.isEmpty()) {

            output.append(
                    "Remove the current source for the simple "
                    + "Req + voltage-source current calculation.\n"
            );

            return output.toString();
        }


        VoltageSource source =
                voltageSources.get(0);


        int positiveNode =
                nodes.getNode(
                        source,
                        0
                );

        int negativeNode =
                nodes.getNode(
                        source,
                        1
                );


        double sourceVoltage =
                source.getValue();


        output.append(
                "SOURCE\n"
        );

        output.append(
                "----------------------------------------\n"
        );

        output.append(
                source.getName()
                + " = "
                + String.format(
                        "%.4f",
                        sourceVoltage
                )
                + " V\n"
        );

        output.append(
                "Positive terminal: Node "
                + positiveNode
                + "\n"
        );

        output.append(
                "Negative terminal: Node "
                + negativeNode
                + "\n\n"
        );


        if (positiveNode == negativeNode) {

            output.append(
                    "The voltage source is shorted because both "
                    + "terminals are on the same node.\n"
            );

            output.append(
                    "Req = 0 ohms\n"
            );

            output.append(
                    "An ideal voltage source across 0 ohms would "
                    + "produce unbounded current.\n"
            );

            return output.toString();
        }


        // Find nodes that are actually connected to the
        // positive source terminal through resistors.
        Set<Integer> reachable =
                findReachableNodes(
                        positiveNode,
                        edges
                );


        if (!reachable.contains(negativeNode)) {

            output.append(
                    "OPEN CIRCUIT\n"
            );

            output.append(
                    "There is no resistor path from the positive "
                    + "source terminal to the negative terminal.\n\n"
            );

            output.append(
                    "Req = infinity\n"
            );

            output.append(
                    "I = 0 A\n"
            );

            return output.toString();
        }


        // Solve once using a 1 V test source.
        // Req = 1 V / Itest.
        Map<Integer, Double> testVoltages =
                solveResistorNodeVoltages(
                        reachable,
                        edges,
                        positiveNode,
                        1.0,
                        negativeNode,
                        0.0
                );


        double testCurrent =
                currentLeavingNode(
                        positiveNode,
                        testVoltages,
                        edges
                );


        if (Math.abs(testCurrent) < 1e-12) {

            output.append(
                    "The resistor network behaves as an open circuit.\n"
            );

            output.append(
                    "Req = infinity\n"
            );

            output.append(
                    "I = 0 A\n"
            );

            return output.toString();
        }


        double equivalentResistance =
                1.0 / testCurrent;


        if (equivalentResistance < 0) {
            equivalentResistance =
                    -equivalentResistance;
        }


        double sourceCurrent =
                sourceVoltage
                /
                equivalentResistance;


        output.append(
                "EQUIVALENT RESISTANCE\n"
        );

        output.append(
                "----------------------------------------\n"
        );

        output.append(
                "Using a 1 V test source across the resistor network:\n"
        );

        output.append(
                "Itest = "
                + String.format(
                        "%.6f",
                        Math.abs(testCurrent)
                )
                + " A\n"
        );

        output.append(
                "Req = Vtest / Itest\n"
        );

        output.append(
                "Req = 1 / "
                + String.format(
                        "%.6f",
                        Math.abs(testCurrent)
                )
                + "\n"
        );

        output.append(
                "Req = "
                + String.format(
                        "%.4f",
                        equivalentResistance
                )
                + " ohms\n\n"
        );


        output.append(
                "SOURCE CURRENT\n"
        );

        output.append(
                "----------------------------------------\n"
        );

        output.append(
                "I = V / Req\n"
        );

        output.append(
                "I = "
                + String.format(
                        "%.4f",
                        sourceVoltage
                )
                + " / "
                + String.format(
                        "%.4f",
                        equivalentResistance
                )
                + "\n"
        );

        output.append(
                "I = "
                + String.format(
                        "%.6f",
                        sourceCurrent
                )
                + " A\n\n"
        );


        // Solve actual node voltages using the real source voltage.
        Map<Integer, Double> actualVoltages =
                solveResistorNodeVoltages(
                        reachable,
                        edges,
                        positiveNode,
                        sourceVoltage,
                        negativeNode,
                        0.0
                );


        output.append(
                "NODE VOLTAGES\n"
        );

        output.append(
                "----------------------------------------\n"
        );


        ArrayList<Integer> sortedNodes =
                new ArrayList<>(
                        reachable
                );

        Collections.sort(
                sortedNodes
        );


        for (int node : sortedNodes) {

            output.append(
                    "Node "
                    + node
                    + " = "
                    + String.format(
                            "%.4f",
                            actualVoltages.get(node)
                    )
                    + " V"
            );


            if (node == negativeNode) {

                output.append(
                        "   (reference)"
                );
            }


            output.append("\n");
        }


        output.append("\n");


        output.append(
                "RESISTOR CURRENTS\n"
        );

        output.append(
                "----------------------------------------\n"
        );


        for (ResistorEdge edge : edges) {

            if (
                    !actualVoltages.containsKey(edge.nodeA)
                    ||
                    !actualVoltages.containsKey(edge.nodeB)
            ) {
                continue;
            }


            double voltageA =
                    actualVoltages.get(edge.nodeA);

            double voltageB =
                    actualVoltages.get(edge.nodeB);


            double current =
                    (voltageA - voltageB)
                    /
                    edge.resistor.getValue();


            output.append(
                    edge.resistor.getName()
                    + ": "
                    + String.format(
                            "%.6f",
                            current
                    )
                    + " A"
                    + "   (Node "
                    + edge.nodeA
                    + " -> Node "
                    + edge.nodeB
                    + ")\n"
            );
        }


        return output.toString();
    }


    // =========================================================
    // SHOW PARALLEL RESISTOR GROUPS
    // =========================================================

    private static void appendParallelGroups(
            StringBuilder output,
            ArrayList<ResistorEdge> edges
    ) {

        Map<String, ArrayList<ResistorEdge>> groups =
                new LinkedHashMap<>();


        for (ResistorEdge edge : edges) {

            int low =
                    Math.min(
                            edge.nodeA,
                            edge.nodeB
                    );

            int high =
                    Math.max(
                            edge.nodeA,
                            edge.nodeB
                    );


            String key =
                    low + ":" + high;


            groups.computeIfAbsent(
                    key,
                    value -> new ArrayList<>()
            ).add(edge);
        }


        boolean found =
                false;


        for (ArrayList<ResistorEdge> group : groups.values()) {

            if (group.size() < 2) {
                continue;
            }


            found = true;


            output.append(
                    "PARALLEL GROUP\n"
            );

            output.append(
                    "----------------------------------------\n"
            );


            double inverse =
                    0.0;


            for (ResistorEdge edge : group) {

                output.append(
                        edge.resistor.getName()
                        + " = "
                        + String.format(
                                "%.4f",
                                edge.resistor.getValue()
                        )
                        + " ohms\n"
                );


                inverse +=
                        1.0
                        /
                        edge.resistor.getValue();
            }


            double req =
                    1.0 / inverse;


            output.append("\n1 / Req = ");


            for (int i = 0; i < group.size(); i++) {

                if (i > 0) {
                    output.append(" + ");
                }


                output.append(
                        "1/"
                        + group.get(i)
                        .resistor
                        .getName()
                );
            }


            output.append("\n");


            output.append(
                    "Req(parallel group) = "
                    + String.format(
                            "%.4f",
                            req
                    )
                    + " ohms\n\n"
            );
        }


        if (!found) {

            output.append(
                    "No direct parallel resistor group detected.\n\n"
            );
        }
    }


    // =========================================================
    // FIND NODES CONNECTED THROUGH RESISTORS
    // =========================================================

    private static Set<Integer> findReachableNodes(
            int startNode,
            ArrayList<ResistorEdge> edges
    ) {

        Set<Integer> visited =
                new HashSet<>();

        Queue<Integer> queue =
                new LinkedList<>();


        visited.add(
                startNode
        );

        queue.add(
                startNode
        );


        while (!queue.isEmpty()) {

            int node =
                    queue.remove();


            for (ResistorEdge edge : edges) {

                if (
                        edge.nodeA != node
                        &&
                        edge.nodeB != node
                ) {
                    continue;
                }


                int other =
                        edge.otherNode(node);


                if (!visited.contains(other)) {

                    visited.add(other);
                    queue.add(other);
                }
            }
        }


        return visited;
    }


    // =========================================================
    // NODAL ANALYSIS FOR A RESISTOR NETWORK
    //
    // knownNodeA = fixed voltage A
    // knownNodeB = fixed voltage B
    //
    // Every other reachable node is solved with KCL.
    // =========================================================

    private static Map<Integer, Double> solveResistorNodeVoltages(
            Set<Integer> reachable,
            ArrayList<ResistorEdge> edges,
            int knownNodeA,
            double knownVoltageA,
            int knownNodeB,
            double knownVoltageB
    ) {

        Map<Integer, Double> voltages =
                new HashMap<>();


        voltages.put(
                knownNodeA,
                knownVoltageA
        );

        voltages.put(
                knownNodeB,
                knownVoltageB
        );


        ArrayList<Integer> unknownNodes =
                new ArrayList<>();


        for (int node : reachable) {

            if (
                    node != knownNodeA
                    &&
                    node != knownNodeB
            ) {

                unknownNodes.add(node);
            }
        }


        Collections.sort(
                unknownNodes
        );


        if (unknownNodes.isEmpty()) {

            return voltages;
        }


        Map<Integer, Integer> matrixIndex =
                new HashMap<>();


        for (int i = 0; i < unknownNodes.size(); i++) {

            matrixIndex.put(
                    unknownNodes.get(i),
                    i
            );
        }


        int size =
                unknownNodes.size();


        double[][] matrix =
                new double[size][size];

        double[] rightSide =
                new double[size];


        for (int row = 0; row < size; row++) {

            int node =
                    unknownNodes.get(row);


            for (ResistorEdge edge : edges) {

                if (
                        edge.nodeA != node
                        &&
                        edge.nodeB != node
                ) {
                    continue;
                }


                int other =
                        edge.otherNode(node);


                if (!reachable.contains(other)) {
                    continue;
                }


                double conductance =
                        1.0
                        /
                        edge.resistor.getValue();


                matrix[row][row] +=
                        conductance;


                if (other == knownNodeA) {

                    rightSide[row] +=
                            conductance
                            *
                            knownVoltageA;

                } else if (
                        other == knownNodeB
                ) {

                    rightSide[row] +=
                            conductance
                            *
                            knownVoltageB;

                } else {

                    Integer column =
                            matrixIndex.get(other);


                    if (column != null) {

                        matrix[row][column] -=
                                conductance;
                    }
                }
            }
        }


        double[] solution =
                gaussianElimination(
                        matrix,
                        rightSide
                );


        for (int i = 0; i < unknownNodes.size(); i++) {

            voltages.put(
                    unknownNodes.get(i),
                    solution[i]
            );
        }


        return voltages;
    }


    // =========================================================
    // CURRENT LEAVING ONE NODE THROUGH ALL RESISTORS
    // =========================================================

    private static double currentLeavingNode(
            int node,
            Map<Integer, Double> voltages,
            ArrayList<ResistorEdge> edges
    ) {

        double current =
                0.0;


        for (ResistorEdge edge : edges) {

            if (
                    edge.nodeA != node
                    &&
                    edge.nodeB != node
            ) {
                continue;
            }


            int other =
                    edge.otherNode(node);


            if (!voltages.containsKey(other)) {
                continue;
            }


            double nodeVoltage =
                    voltages.get(node);

            double otherVoltage =
                    voltages.get(other);


            current +=
                    (nodeVoltage - otherVoltage)
                    /
                    edge.resistor.getValue();
        }


        return current;
    }


    // =========================================================
    // GAUSSIAN ELIMINATION
    // =========================================================

    private static double[] gaussianElimination(
            double[][] matrix,
            double[] rightSide
    ) {

        int size =
                rightSide.length;


        double[][] augmented =
                new double[size][size + 1];


        for (int row = 0; row < size; row++) {

            for (int column = 0; column < size; column++) {

                augmented[row][column] =
                        matrix[row][column];
            }

            augmented[row][size] =
                    rightSide[row];
        }


        for (int column = 0; column < size; column++) {

            int pivot =
                    column;


            for (int row = column + 1; row < size; row++) {

                if (
                        Math.abs(
                                augmented[row][column]
                        )
                        >
                        Math.abs(
                                augmented[pivot][column]
                        )
                ) {

                    pivot = row;
                }
            }


            if (
                    Math.abs(
                            augmented[pivot][column]
                    )
                    <
                    1e-12
            ) {

                throw new IllegalArgumentException(
                        "Circuit equations are singular."
                );
            }


            double[] temporary =
                    augmented[column];

            augmented[column] =
                    augmented[pivot];

            augmented[pivot] =
                    temporary;


            double pivotValue =
                    augmented[column][column];


            for (int j = column; j <= size; j++) {

                augmented[column][j] /=
                        pivotValue;
            }


            for (int row = 0; row < size; row++) {

                if (row == column) {
                    continue;
                }


                double factor =
                        augmented[row][column];


                for (int j = column; j <= size; j++) {

                    augmented[row][j] -=
                            factor
                            *
                            augmented[column][j];
                }
            }
        }


        double[] result =
                new double[size];


        for (int i = 0; i < size; i++) {

            result[i] =
                    augmented[i][size];
        }


        return result;
    }
}
