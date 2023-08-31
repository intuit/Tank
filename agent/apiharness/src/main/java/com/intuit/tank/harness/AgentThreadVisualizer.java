package com.intuit.tank.harness;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.APITestHarness;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class AgentThreadVisualizer {


    private XYSeries agentThreadsSeries;
    private Timer updateTimer;

    private APITestHarness apiTestHarness;

    public AgentThreadVisualizer(APITestHarness apiTestHarness) {
        this.apiTestHarness = apiTestHarness;

        agentThreadsSeries = new XYSeries("Total Concurrent Users");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(agentThreadsSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Total Concurrent Users",
                "Time (seconds)",
                "Total Concurrent Users",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        agentThreadsSeries.add(0, 0);

        // Timer to update the graph every second
        updateTimer = new Timer(1000, new ActionListener() {
            private long startTime = System.currentTimeMillis();


            @Override
            public void actionPerformed(ActionEvent e) {
                double t = (System.currentTimeMillis() - startTime) / 1000.0;
                int currentConcurrentUsers = apiTestHarness.getCurrentUsers();
                agentThreadsSeries.add(t, currentConcurrentUsers);
            }
        });
        updateTimer.start();
    }
}
