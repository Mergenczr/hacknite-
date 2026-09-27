import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("EE Midterm Helper");

            frame.setSize(1100, 700);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            CircuitCanvas canvas = new CircuitCanvas();

            JPanel toolbar = new JPanel();

            JButton solveButton =
                    new JButton("Solve Circuit");

            JButton transformButton =
                    new JButton("Source Transform");

            JButton deleteButton =
                    new JButton("Delete Selected");

            JButton clearButton =
                    new JButton("Clear Board");


            toolbar.add(solveButton);
            toolbar.add(transformButton);
            toolbar.add(deleteButton);
            toolbar.add(clearButton);


            solveButton.addActionListener(
                    e -> canvas.solveCircuit()
            );

            transformButton.addActionListener(
                    e -> canvas.sourceTransform()
            );

            deleteButton.addActionListener(
                    e -> canvas.deleteSelected()
            );

            clearButton.addActionListener(
                    e -> canvas.clearBoard()
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