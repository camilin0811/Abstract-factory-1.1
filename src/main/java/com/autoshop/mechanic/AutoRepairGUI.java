package com.autoshop.mechanic;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Swing desktop front-end for the shop. Lets the user pick a vehicle
 * family, type in symptoms, and run the AI diagnosis without leaving a
 * graphical window. The Groq call runs on a background thread
 * (SwingWorker) so the UI never freezes while waiting on the network.
 */
public class AutoRepairGUI extends JFrame {

    private static final Color BRAND = new Color(0xC8, 0x72, 0x0F);
    private static final Color BRAND_DARK = new Color(0x22, 0x26, 0x2C);
    private static final Color OK = new Color(0x1F, 0x7A, 0x63);
    private static final Color ERROR = new Color(0xB0, 0x2A, 0x2A);

    private final JRadioButton gasolineRadio = new JRadioButton("Gasoline", true);
    private final JRadioButton electricRadio = new JRadioButton("Electric");
    private final JTextField plateField = new JTextField();
    private final JTextField symptomField = new JTextField();
    private final DefaultListModel<String> symptomListModel = new DefaultListModel<>();
    private final JList<String> symptomList = new JList<>(symptomListModel);
    private final JButton runButton = new JButton("Run AI Diagnosis");
    private final JLabel statusLabel = new JLabel(" ");

    private final JLabel engineLabel = new JLabel("—");
    private final JLabel partLabel = new JLabel("—");
    private final JLabel faultLabel = new JLabel("—");
    private final JProgressBar confidenceBar = new JProgressBar(0, 100);
    private final JTextArea recommendationArea = new JTextArea(4, 24);

    public AutoRepairGUI() {
        super("AutoAI Repair Shop");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Falls back to the default cross-platform look and feel.
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(16, 16, 16, 16));
        center.add(buildFormPanel());
        center.add(Box.createVerticalStrut(14));
        center.add(buildResultsPanel());
        add(center, BorderLayout.CENTER);

        add(buildStatusBar(), BorderLayout.SOUTH);

        runButton.addActionListener(this::onRunDiagnosis);
        runButton.setBackground(BRAND);
        runButton.setForeground(Color.WHITE);
        runButton.setFocusPainted(false);

        setMinimumSize(new Dimension(560, 620));
        pack();
        setLocationRelativeTo(null);
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BRAND_DARK);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("AutoAI Repair Shop");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Abstract Factory pattern + live Groq AI diagnostics");
        subtitle.setForeground(new Color(0xC9, 0xCF, 0xD6));
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 12f));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(subtitle);
        return header;
    }

    private JComponent buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Vehicle Details"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        ButtonGroup group = new ButtonGroup();
        group.add(gasolineRadio);
        group.add(electricRadio);

        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("Vehicle type:"), c);
        c.gridx = 1;
        c.gridwidth = 2;
        JPanel radios = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        radios.add(gasolineRadio);
        radios.add(electricRadio);
        panel.add(radios, c);
        c.gridwidth = 1;

        c.gridx = 0;
        c.gridy = 1;
        panel.add(new JLabel("License plate:"), c);
        c.gridx = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        panel.add(plateField, c);
        c.gridwidth = 1;
        c.weightx = 0;

        c.gridx = 0;
        c.gridy = 2;
        panel.add(new JLabel("Symptom:"), c);
        c.gridx = 1;
        c.weightx = 1;
        panel.add(symptomField, c);
        c.gridx = 2;
        c.weightx = 0;
        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(this::onAddSymptom);
        panel.add(addBtn, c);
        symptomField.addActionListener(this::onAddSymptom);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 3;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        symptomList.setVisibleRowCount(4);
        panel.add(new JScrollPane(symptomList), c);
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 3;
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton removeBtn = new JButton("Remove selected");
        removeBtn.addActionListener(e -> {
            int idx = symptomList.getSelectedIndex();
            if (idx >= 0) {
                symptomListModel.remove(idx);
            }
        });
        actions.add(removeBtn);
        actions.add(runButton);
        panel.add(actions, c);

        return panel;
    }

    private JComponent buildResultsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("AI Diagnosis"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("Engine:"), c);
        c.gridx = 1;
        c.weightx = 1;
        panel.add(engineLabel, c);
        c.weightx = 0;

        c.gridx = 0;
        c.gridy = 1;
        panel.add(new JLabel("Spare part:"), c);
        c.gridx = 1;
        panel.add(partLabel, c);

        c.gridx = 0;
        c.gridy = 2;
        panel.add(new JLabel("Fault:"), c);
        c.gridx = 1;
        faultLabel.setFont(faultLabel.getFont().deriveFont(Font.BOLD));
        faultLabel.setForeground(BRAND);
        panel.add(faultLabel, c);

        c.gridx = 0;
        c.gridy = 3;
        panel.add(new JLabel("Confidence:"), c);
        c.gridx = 1;
        confidenceBar.setStringPainted(true);
        panel.add(confidenceBar, c);

        c.gridx = 0;
        c.gridy = 4;
        c.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Recommendation:"), c);
        c.gridx = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        recommendationArea.setLineWrap(true);
        recommendationArea.setWrapStyleWord(true);
        recommendationArea.setEditable(false);
        recommendationArea.setBackground(panel.getBackground());
        panel.add(new JScrollPane(recommendationArea), c);

        return panel;
    }

    private JComponent buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBorder(new EmptyBorder(6, 12, 6, 12));
        statusLabel.setForeground(Color.GRAY);
        bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }

    private void onAddSymptom(ActionEvent e) {
        String text = symptomField.getText().trim();
        if (!text.isEmpty()) {
            symptomListModel.addElement(text);
            symptomField.setText("");
        }
        symptomField.requestFocus();
    }

    private void onRunDiagnosis(ActionEvent e) {
        String plate = plateField.getText().trim();
        if (plate.isEmpty()) {
            showStatus("Please enter a license plate.", ERROR);
            return;
        }

        List<String> symptoms = new ArrayList<>();
        for (int i = 0; i < symptomListModel.size(); i++) {
            symptoms.add(symptomListModel.get(i));
        }
        if (symptoms.isEmpty()) {
            showStatus("Add at least one symptom.", ERROR);
            return;
        }

        VehicleFactory factory = electricRadio.isSelected()
                ? new ElectricVehicleFactory()
                : new GasolineVehicleFactory();

        runButton.setEnabled(false);
        showStatus("Contacting Groq AI...", Color.GRAY);

        new SwingWorker<Void, Void>() {
            private Engine engine;
            private SparePart part;
            private DiagnosticResult result;

            @Override
            protected Void doInBackground() {
                engine = factory.createEngine();
                part = factory.createSparePart();
                result = factory.createDiagnosticAI().diagnose(symptoms);
                return null;
            }

            @Override
            protected void done() {
                engineLabel.setText(engine.getSpecs());
                partLabel.setText(String.format("%s ($%.2f)", part.getPartName(), part.getPrice()));
                faultLabel.setText(result.getProbableFault());
                confidenceBar.setValue(result.getConfidencePercentage());
                confidenceBar.setString(result.getConfidencePercentage() + "%");
                recommendationArea.setText(result.getRecommendation());

                runButton.setEnabled(true);
                showStatus("Diagnosis for " + plate + " complete.", OK);
            }
        }.execute();
    }

    private void showStatus(String text, Color color) {
        statusLabel.setForeground(color);
        statusLabel.setText(text);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AutoRepairGUI().setVisible(true));
    }
}
