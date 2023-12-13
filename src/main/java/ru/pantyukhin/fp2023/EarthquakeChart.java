package ru.pantyukhin.fp2023;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

public class EarthquakeChart extends JFrame {

    public EarthquakeChart(Map<Integer, Integer> earthquakeCountsByYear) {
        super("Earthquake Counts by Year");
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<Integer, Integer> entry : earthquakeCountsByYear.entrySet()) {
            dataset.addValue(entry.getValue(), "Earthquakes", String.valueOf(entry.getKey()));
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Earthquake Counts by Year",
                "Year",
                "Number of Earthquakes",
                dataset
        );

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        setContentPane(chartPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose(); // закрытие текущего окна
                System.exit(0); // завершение процесса
            }
        });
    }
}
