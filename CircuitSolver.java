import java.util.ArrayList;

public class CircuitSolver {

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


    public static String solve(
            ArrayList<CircuitComponent> components,
            ArrayList<Wire> wires
    ) {

        StringBuilder output =
                new StringBuilder();

        output.append("CIRCUIT ANALYSIS\n");
        output.append("====================\n\n");

        output.append(
                "Components: "
                + components.size()
                + "\n"
        );

        output.append(
                "Connections: "
                + wires.size()
                + "\n\n"
        );

        for (CircuitComponent component : components) {

            output.append(
                    component.getName()
                    + " = "
                    + String.format(
                            "%.3f",
                            component.getValue()
                    )
                    + " "
                    + component.getUnit()
                    + "\n"
            );
        }

        return output.toString();
    }
}