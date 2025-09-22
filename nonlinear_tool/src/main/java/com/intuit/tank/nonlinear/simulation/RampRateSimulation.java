package com.intuit.tank.nonlinear.simulation;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class RampRateSimulation {

    // Inputs
    private double endRampRate = 10; // users/sec at end of ramp
    private double d = 10;           // ramp duration (sec)
    private double u = 5;            // user duration (sec)
    private double callsPerScript = 600; // NEW: total calls per user/script (c)

    // Charts & datasets
    private JFreeChart rampRateChart;
    private JFreeChart concurrentUsersChart;
    private JFreeChart tpsChart; // NEW

    private XYSeriesCollection dataset = new XYSeriesCollection();
    private XYSeries series = new XYSeries("Ramp Rate");

    private XYSeriesCollection concurrentUsersDataset = new XYSeriesCollection();
    private XYSeries concurrentUsersSeries = new XYSeries("Concurrent Users");

    private XYSeriesCollection tpsDataset = new XYSeriesCollection(); // NEW
    private XYSeries tpsSeries = new XYSeries("TPS");                 // NEW

    private ChartPanel rampRatePanel;
    private ChartPanel concurrentUsersPanel;
    private ChartPanel tpsPanel; // NEW

    public RampRateSimulation() {
        JFrame frame = new JFrame("Linear Ramp Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // --- Inputs UI ---
        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        JTextField endField = new JTextField("10");
        JTextField dField = new JTextField("10");
        JTextField uField = new JTextField("5");
        JTextField callsField = new JTextField("600"); // NEW
        JButton updateButton = new JButton("Update Graph");

        inputPanel.add(new JLabel("Target Ramp Rate (users/sec):"));
        inputPanel.add(endField);
        inputPanel.add(new JLabel("Ramp Duration (sec):"));
        inputPanel.add(dField);
        inputPanel.add(new JLabel("User Duration (sec):"));
        inputPanel.add(uField);
        inputPanel.add(new JLabel("Calls per Script (total calls per user):")); // NEW
        inputPanel.add(callsField);
        inputPanel.add(updateButton);

        frame.add(inputPanel, BorderLayout.NORTH);

        // --- Datasets -> Charts ---
        dataset.addSeries(series);
        concurrentUsersDataset.addSeries(concurrentUsersSeries);
        tpsDataset.addSeries(tpsSeries); // NEW

        rampRateChart = ChartFactory.createXYLineChart(
                "Ramp Rate vs Time",
                "Time (seconds)",
                "Ramp Rate (users/sec)",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        concurrentUsersChart = ChartFactory.createXYLineChart(
                "Concurrent Users vs Time",
                "Time (seconds)",
                "Concurrent Users",
                concurrentUsersDataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        tpsChart = ChartFactory.createXYLineChart( // NEW
                "TPS vs Time",
                "Time (seconds)",
                "Transactions per Second (TPS)",
                tpsDataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        rampRatePanel = new ChartPanel(rampRateChart);
        concurrentUsersPanel = new ChartPanel(concurrentUsersChart);
        tpsPanel = new ChartPanel(tpsChart); // NEW

        // Stack all three charts vertically
        JPanel charts = new JPanel(new GridLayout(3, 1));
        charts.add(rampRatePanel);
        charts.add(concurrentUsersPanel);
        charts.add(tpsPanel);
        frame.add(charts, BorderLayout.CENTER);

        // --- Button logic ---
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        endRampRate = Double.parseDouble(endField.getText());
                        d = Double.parseDouble(dField.getText());
                        u = Double.parseDouble(uField.getText());
                        callsPerScript = Double.parseDouble(callsField.getText()); // NEW
                        updateGraph();
                        return null;
                    }

                    @Override
                    protected void done() {
                        rampRatePanel.repaint();
                        concurrentUsersPanel.repaint();
                        tpsPanel.repaint(); // NEW
                    }
                };
                worker.execute();
            }
        });

        // Crosshairs for all 3 charts
        XYPlot rampRatePlot = rampRateChart.getXYPlot();
        XYPlot concurrentUsersPlot = concurrentUsersChart.getXYPlot();
        XYPlot tpsPlot = tpsChart.getXYPlot();

        rampRatePlot.setDomainCrosshairVisible(true);
        rampRatePlot.setRangeCrosshairVisible(true);
        concurrentUsersPlot.setDomainCrosshairVisible(true);
        concurrentUsersPlot.setRangeCrosshairVisible(true);
        tpsPlot.setDomainCrosshairVisible(true);       // NEW
        tpsPlot.setRangeCrosshairVisible(true);        // NEW

        rampRatePlot.setDomainCrosshairStroke(new BasicStroke(0.5f));
        rampRatePlot.setRangeCrosshairStroke(new BasicStroke(0.5f));
        rampRatePlot.setDomainCrosshairPaint(Color.BLACK);
        rampRatePlot.setRangeCrosshairPaint(Color.BLACK);

        concurrentUsersPlot.setDomainCrosshairStroke(new BasicStroke(0.5f));
        concurrentUsersPlot.setRangeCrosshairStroke(new BasicStroke(0.5f));
        concurrentUsersPlot.setDomainCrosshairPaint(Color.BLACK);
        concurrentUsersPlot.setRangeCrosshairPaint(Color.BLACK);

        tpsPlot.setDomainCrosshairStroke(new BasicStroke(0.5f)); // NEW
        tpsPlot.setRangeCrosshairStroke(new BasicStroke(0.5f));  // NEW
        tpsPlot.setDomainCrosshairPaint(Color.BLACK);            // NEW
        tpsPlot.setRangeCrosshairPaint(Color.BLACK);             // NEW

        addMouseCrossHair(rampRatePanel, rampRateChart, series);
        addMouseCrossHair(concurrentUsersPanel, concurrentUsersChart, concurrentUsersSeries);
        addMouseCrossHair(tpsPanel, tpsChart, tpsSeries); // NEW

        frame.pack();
        frame.setVisible(true);

        // Render initial graphs
        updateGraph();
    }

    private void addMouseCrossHair(ChartPanel panel, JFreeChart chart, XYSeries series) {
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (series.getItemCount() == 0) return;

                Point2D p = panel.translateScreenToJava2D(e.getPoint());
                XYPlot plot = (XYPlot) chart.getPlot();
                ChartRenderingInfo info = panel.getChartRenderingInfo();
                Rectangle2D dataArea = info.getPlotInfo().getDataArea();

                double mouseXValue = plot.getDomainAxis().java2DToValue(
                        p.getX(), dataArea, plot.getDomainAxisEdge());

                double nearestXValue = series.getX(0).doubleValue();
                for (int i = 1; i < series.getItemCount(); i++) {
                    double currentX = series.getX(i).doubleValue();
                    if (Math.abs(currentX - mouseXValue) < Math.abs(nearestXValue - mouseXValue)) {
                        nearestXValue = currentX;
                    }
                }

                double y = series.getY(series.indexOf(nearestXValue)).doubleValue();
                plot.setDomainCrosshairValue(nearestXValue);
                plot.setRangeCrosshairValue(y);
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                XYPlot plot = (XYPlot) chart.getPlot();
                plot.setDomainCrosshairVisible(false);
                plot.setRangeCrosshairVisible(false);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                XYPlot plot = (XYPlot) chart.getPlot();
                plot.setDomainCrosshairVisible(true);
                plot.setRangeCrosshairVisible(true);
            }
        });
    }

    private void updateGraph() {
        series.clear();
        concurrentUsersSeries.clear();
        tpsSeries.clear(); // NEW

        rampRateChart.getXYPlot().clearAnnotations();
        concurrentUsersChart.getXYPlot().clearAnnotations();
        tpsChart.getXYPlot().clearAnnotations(); // NEW

        // show a few u-lengths beyond d so we reach steady state in the TPS chart
        double horizon = Math.max(d + u * 2, d * 3);

        for (double t = 0; t <= horizon; t += 1) {
            double currentRampRate = linearRampRate(t);
            series.add(t, currentRampRate);

            double concurrentUsers = calculateConcurrentUsers(t);
            if (concurrentUsers > 0) {
                concurrentUsersSeries.add(t, concurrentUsers);

                // TPS = concurrency * (calls per user / user duration)
                double perUserRate = (u > 0) ? (callsPerScript / u) : 0.0;
                double tps = concurrentUsers * perUserRate;
                tpsSeries.add(t, tps);
            }
        }
    }

    // Total arrivals up to t (integral of arrival rate)
    private double calculateTotalUsers(double t) {
        if (t < d) {
            return (endRampRate / (2 * d)) * t * t;
        } else {
            double rampUpUsers = (endRampRate / (2 * d)) * d * d; // = endRampRate * d / 2
            return rampUpUsers + (endRampRate * (t - d));
        }
    }

    // Active users = arrivals in the last u seconds
    private double calculateConcurrentUsers(double t) {
        double totalUsersNow = calculateTotalUsers(t);
        double totalUsersBefore = t - u >= 0 ? calculateTotalUsers(t - u) : 0;
        return totalUsersNow - totalUsersBefore;
    }

    // Linear ramp: 0 -> endRampRate over duration d, then flat
    private double linearRampRate(double t) {
        return (t <= d) ? (endRampRate / d) * t : endRampRate;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RampRateSimulation::new);
    }
}
