package com.intuit.tank.nonlinear.simulation;

import com.intuit.tank.harness.AgentRunData;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.nonlinear.agent.NonlinearTestPlanStarter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class NonlinearRampVisualizer {


    private XYSeries usersSeries;
    private XYSeries rampRateSeries;
    private NonlinearTestPlanStarter testPlanStarter;
    private Timer updateTimer;


    public NonlinearRampVisualizer(NonlinearTestPlanStarter testPlanStarter) {
        this.testPlanStarter = testPlanStarter;


        usersSeries = new XYSeries("Cumulative Users");
        rampRateSeries = new XYSeries("Ramp Rate (users/sec)");


        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(usersSeries);
        dataset.addSeries(rampRateSeries);


        JFreeChart chart = ChartFactory.createXYLineChart(
                "Ramp Up Visualization",
                "Time (seconds)",
                "Number",
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


        // Timer to update the graph every second
        updateTimer = new Timer(1000, new ActionListener() {
            private long startTime = System.currentTimeMillis();
            private int lastUserCount = 0;


            @Override
            public void actionPerformed(ActionEvent e) {
                double t = (System.currentTimeMillis() - startTime) / 1000.0;
                int currentUserCount = testPlanStarter.getThreadsStarted();
                int usersAdded = currentUserCount - lastUserCount;


                usersSeries.add(t, currentUserCount);
                rampRateSeries.add(t, usersAdded);  // This is the ramp rate (users/sec)


                lastUserCount = currentUserCount;
            }
        });
        updateTimer.start();
    }


    public static void main(String[] args) {
        HDTestPlan plan = new HDTestPlan();
        plan.setTestPlanName("Main");
        plan.setUserPercentage(100);
        AgentRunData agentRunData = new AgentRunData();
        agentRunData.setRampTimeMillis(30000);
        agentRunData.setSimulationTimeMillis(60000);
        agentRunData.setNumStartUsers(0);
        agentRunData.setUserInterval(1);
        NonlinearTestPlanStarter testPlanStarter = new NonlinearTestPlanStarter(plan, 3000, null, agentRunData, 0, 10, 1.2);
        SwingUtilities.invokeLater(() -> new NonlinearRampVisualizer(testPlanStarter));

        // Start your test plan
        new Thread(testPlanStarter).start();
    }
}





























