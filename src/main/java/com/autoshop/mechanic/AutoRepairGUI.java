package com.autoshop.mechanic;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Swing desktop front-end for the shop. Lets the user pick a vehicle
 * family, type in symptoms, and run the AI diagnosis without leaving a
 * graphical window. The Groq call runs on a background thread
 * (SwingWorker) so the UI never freezes while waiting on the network.
 *
 * The accent color follows the selected vehicle family (amber for
 * gasoline, teal for electric) across the toggle, the primary button and
 * the fault highlight, so the whole panel visually "commits" to the
 * chosen family the same way the Abstract Factory does under the hood.
 */
public class AutoRepairGUI extends JFrame {

    private static final Color BG = new Color(0xF2, 0xF4, 0xF8);
    private static final Color CARD = Color.WHITE;
    private static final Color BORDER = new Color(0xE1, 0xE5, 0xEA);
    private static final Color TEXT = new Color(0x1F, 0x25, 0x30);
    private static final Color TEXT_DIM = new Color(0x66, 0x70, 0x7B);
    private static final Color NAVY = new Color(0x1B, 0x24, 0x30);
    private static final Color NAVY_2 = new Color(0x2A, 0x36, 0x46);

    private static final Color GAS = new Color(0xE0, 0x70, 0x1B);
    private static final Color GAS_HOVER = new Color(0xC7, 0x62, 0x14);
    private static final Color GAS_SOFT = new Color(0xFB, 0xEA, 0xD9);

    private static final Color EV = new Color(0x12, 0x83, 0x6B);
    private static final Color EV_HOVER = new Color(0x0E, 0x6E, 0x5A);
    private static final Color EV_SOFT = new Color(0xDC, 0xF1, 0xEC);

    private static final Color ERROR = new Color(0xC4, 0x35, 0x35);
    private static final Color OK = new Color(0x1F, 0x9D, 0x55);

    private final PillToggleButton gasolineToggle = new PillToggleButton("⛽ Gasoline", GAS, Color.WHITE, TEXT_DIM);
    private final PillToggleButton electricToggle = new PillToggleButton("⚡ Electric", EV, Color.WHITE, TEXT_DIM);
    private final JTextField plateField = new JTextField();
    private final JTextField symptomField = new JTextField();
    private final DefaultListModel<String> symptomListModel = new DefaultListModel<>();
    private final JList<String> symptomList = new JList<>(symptomListModel);
    private final RoundedButton runButton = new RoundedButton("▶  Run AI Diagnosis", GAS, GAS_HOVER, Color.WHITE);
    private final JLabel statusLabel = new JLabel(" ");

    private final RoundedPanel faultCard = new RoundedPanel(10, GAS_SOFT, null);
    private final JLabel engineLabel = new JLabel("—");
    private final JLabel partLabel = new JLabel("—");
    private final JLabel faultLabel = new JLabel("Run a diagnosis to see results here");
    private final ConfidenceGauge confidenceGauge = new ConfidenceGauge();
    private final JTextArea recommendationArea = new JTextArea(4, 24);

    public AutoRepairGUI() {
        super("AutoAI Repair Shop");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Falls back to the default cross-platform look and feel.
        }

        getContentPane().setBackground(BG);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(1, 2, 18, 0));
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(18, 18, 18, 18));
        body.add(buildFormCard());
        body.add(buildResultsCard());
        add(body, BorderLayout.CENTER);

        add(buildStatusBar(), BorderLayout.SOUTH);

        ButtonGroup group = new ButtonGroup();
        group.add(gasolineToggle);
        group.add(electricToggle);
        gasolineToggle.setSelected(true);
        gasolineToggle.addActionListener(e -> applyAccent(GAS, GAS_HOVER, GAS_SOFT));
        electricToggle.addActionListener(e -> applyAccent(EV, EV_HOVER, EV_SOFT));

        runButton.addActionListener(this::onRunDiagnosis);

        setMinimumSize(new Dimension(940, 660));
        pack();
        setLocationRelativeTo(null);
    }

    private void applyAccent(Color base, Color hover, Color soft) {
        runButton.setAccent(base, hover);
        faultCard.setBackground(soft);
        faultLabel.setForeground(base.darker());
        faultCard.repaint();
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(NAVY);
        header.setBorder(new EmptyBorder(20, 26, 20, 26));

        JLabel title = new JLabel("🔧  AutoAI Repair Shop");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Abstract Factory pattern  ·  live diagnostics powered by Groq AI");
        subtitle.setForeground(new Color(0xB9, 0xC3, 0xCF));
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 12.5f));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setBorder(new EmptyBorder(4, 0, 0, 0));

        header.add(title);
        header.add(subtitle);
        return header;
    }

    private JComponent buildFormCard() {
        RoundedPanel card = new RoundedPanel(14, CARD, BORDER);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(20, 22, 20, 22));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 0, 6, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        card.add(sectionLabel("VEHICLE DETAILS"), c);

        c.gridy = 1;
        JPanel toggleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toggleRow.setOpaque(false);
        toggleRow.add(gasolineToggle);
        toggleRow.add(electricToggle);
        card.add(toggleRow, c);

        c.gridy = 2;
        card.add(fieldLabel("LICENSE PLATE"), c);
        c.gridy = 3;
        styleField(plateField, "e.g. ABC-123");
        card.add(plateField, c);

        c.gridy = 4;
        c.insets = new Insets(16, 0, 6, 0);
        card.add(fieldLabel("SYMPTOMS"), c);
        c.insets = new Insets(6, 0, 6, 0);

        c.gridy = 5;
        c.gridwidth = 1;
        c.weightx = 1;
        styleField(symptomField, "e.g. difficulty starting");
        card.add(symptomField, c);
        c.gridx = 1;
        c.weightx = 0;
        RoundedButton addBtn = new RoundedButton("Add", new Color(0x2D, 0x6C, 0xDF), new Color(0x22, 0x57, 0xB8), Color.WHITE);
        addBtn.addActionListener(this::onAddSymptom);
        card.add(addBtn, c);
        symptomField.addActionListener(this::onAddSymptom);

        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 2;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        symptomList.setCellRenderer(new SymptomChipRenderer());
        symptomList.setFixedCellHeight(-1);
        symptomList.setBackground(CARD);
        symptomList.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        JScrollPane symptomScroll = new JScrollPane(symptomList);
        symptomScroll.setBorder(new LineBorder(BORDER, 1, true));
        symptomScroll.setPreferredSize(new Dimension(10, 140));
        card.add(symptomScroll, c);
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridy = 7;
        c.insets = new Insets(10, 0, 0, 0);
        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        RoundedButton removeBtn = new RoundedButton("Remove selected", new Color(0xEE, 0xF1, 0xF5), new Color(0xE2, 0xE6, 0xEC), TEXT_DIM);
        removeBtn.addActionListener(e -> {
            int idx = symptomList.getSelectedIndex();
            if (idx >= 0) {
                symptomListModel.remove(idx);
            }
        });
        actions.add(removeBtn, BorderLayout.WEST);
        actions.add(runButton, BorderLayout.EAST);
        card.add(actions, c);

        return card;
    }

    private JComponent buildResultsCard() {
        RoundedPanel card = new RoundedPanel(14, CARD, BORDER);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(20, 22, 20, 22));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 0, 6, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        card.add(sectionLabel("AI DIAGNOSIS"), c);

        c.gridy = 1;
        JPanel statsRow = new JPanel(new GridLayout(1, 2, 12, 0));
        statsRow.setOpaque(false);
        statsRow.add(statCard("⚙️  ENGINE", engineLabel));
        statsRow.add(statCard("🧰  SPARE PART", partLabel));
        card.add(statsRow, c);

        c.gridy = 2;
        c.insets = new Insets(16, 0, 6, 0);
        faultCard.setLayout(new BorderLayout());
        faultCard.setBorder(new EmptyBorder(14, 16, 14, 16));
        faultLabel.setFont(faultLabel.getFont().deriveFont(Font.BOLD, 16f));
        faultLabel.setForeground(GAS.darker());
        faultCard.add(faultLabel, BorderLayout.CENTER);
        card.add(faultCard, c);
        c.insets = new Insets(6, 0, 6, 0);

        c.gridy = 3;
        c.insets = new Insets(14, 0, 4, 0);
        card.add(fieldLabel("CONFIDENCE"), c);
        c.insets = new Insets(4, 0, 6, 0);
        c.gridy = 4;
        confidenceGauge.setValue(0);
        card.add(confidenceGauge, c);

        c.gridy = 5;
        c.insets = new Insets(14, 0, 4, 0);
        card.add(fieldLabel("RECOMMENDATION"), c);
        c.gridy = 6;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        recommendationArea.setLineWrap(true);
        recommendationArea.setWrapStyleWord(true);
        recommendationArea.setEditable(false);
        recommendationArea.setBackground(new Color(0xF7, 0xF8, 0xFA));
        recommendationArea.setForeground(TEXT);
        recommendationArea.setFont(recommendationArea.getFont().deriveFont(13f));
        recommendationArea.setBorder(new EmptyBorder(10, 12, 10, 12));
        JScrollPane recoScroll = new JScrollPane(recommendationArea);
        recoScroll.setBorder(new LineBorder(BORDER, 1, true));
        card.add(recoScroll, c);

        return card;
    }

    private JComponent statCard(String title, JLabel valueLabel) {
        RoundedPanel panel = new RoundedPanel(10, new Color(0xF7, 0xF8, 0xFA), BORDER);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 10.5f));
        titleLabel.setForeground(TEXT_DIM);

        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.PLAIN, 12.5f));
        valueLabel.setForeground(TEXT);
        valueLabel.setBorder(new EmptyBorder(4, 0, 0, 0));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.SOUTH);
        return panel;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 11.5f));
        label.setForeground(TEXT_DIM);
        return label;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 10.5f));
        label.setForeground(TEXT_DIM);
        return label;
    }

    private void styleField(JTextField field, String placeholderTooltip) {
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(9, 12, 9, 12)));
        field.setFont(field.getFont().deriveFont(13.5f));
        field.setToolTipText(placeholderTooltip);
    }

    private JComponent buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG);
        bar.setBorder(new EmptyBorder(4, 22, 14, 22));
        statusLabel.setForeground(TEXT_DIM);
        statusLabel.setFont(statusLabel.getFont().deriveFont(12f));
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

        VehicleFactory factory = electricToggle.isSelected()
                ? new ElectricVehicleFactory()
                : new GasolineVehicleFactory();

        runButton.setEnabled(false);
        showStatus("Contacting Groq AI...", TEXT_DIM);

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
                runButton.setEnabled(true);
                try {
                    get();
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    showStatus("Unexpected error: " + cause.getMessage(), ERROR);
                    return;
                }

                engineLabel.setText("<html>" + engine.getSpecs() + "</html>");
                partLabel.setText(String.format("<html>%s ($%.2f)</html>", part.getPartName(), part.getPrice()));
                faultLabel.setText(result.getProbableFault());
                confidenceGauge.setValue(result.getConfidencePercentage());
                recommendationArea.setText(result.getRecommendation());

                boolean isError = result.getConfidencePercentage() == 0
                        && (result.getProbableFault().equals("AI not configured")
                        || result.getProbableFault().startsWith("Error contacting"));
                if (isError) {
                    showStatus(result.getProbableFault() + " — see Recommendation for details.", ERROR);
                } else {
                    showStatus("Diagnosis for " + plate + " complete.", OK);
                }
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
