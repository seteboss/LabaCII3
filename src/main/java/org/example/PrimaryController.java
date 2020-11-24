package org.example;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.LineChart.SortingPolicy;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.example.entity.AntType;
import org.example.entity.CityType;
import org.example.service.AntAlgorithm;

public class PrimaryController {

    @FXML
    private Button start;

    @FXML
    private LineChart<Number,Number> graph;

    @FXML
    private NumberAxis axisX;

    @FXML
    private TextField alpha;

    @FXML
    private TextField beta;

    @FXML
    private TextField rho;

    @FXML
    private TextField city;

    @FXML
    private TextField ant;

    @FXML
    private TextField q;

    @FXML
    void initialize(){
        AntAlgorithm antAlgorithm = new AntAlgorithm();
        start.setOnAction(actionEvent -> {
            double alphaVal = Double.parseDouble(alpha.getText());
            double betaVal = Double.parseDouble(beta.getText());
            double rhoVal = Double.parseDouble(rho.getText());
            int countVal = Integer.parseInt(city.getText());
            double qval = Double.parseDouble(q.getText());
            int countAntVal = Integer.parseInt(ant.getText());
            antAlgorithm.setAlpha(alphaVal);
            antAlgorithm.setBeta(betaVal);
            antAlgorithm.setRho(rhoVal);
            antAlgorithm.setQval(qval);
            antAlgorithm.setMaxAnts(countAntVal);
            antAlgorithm.setMaxCities(countVal);
            antAlgorithm.setMaxTour(countVal * antAlgorithm.getMaxDistance());
            antAlgorithm.setMaxTime(countVal * antAlgorithm.getMaxTours());
            antAlgorithm.setBest((double)antAlgorithm.getMaxTour());
            antAlgorithm.setInitPheromone(1.0 / countVal);
            antAlgorithm.perform();
            XYChart.Series<Number, Number> numberSeries = initGraph();

            renderGraph(numberSeries, antAlgorithm);
        });

    }

    XYChart.Series<Number,Number> initGraph(){
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Решение задачи коммивояжера с помощью алгоритма муравья");
        axisX.setAutoRanging(false);
        axisX.setTickLabelFormatter(new NumberAxis.DefaultFormatter(axisX) {
            @Override
            public String toString(Number value) {
                return String.format("%7.1f", value.doubleValue());
            }
        });
        return series;
    }

    void renderGraph(XYChart.Series<Number,Number> series,  AntAlgorithm antAlgorithm){
        series.getData().clear();
        graph.getData().clear();
        CityType[] cityTypes = antAlgorithm.getCities();
        AntType antType = antAlgorithm.getBest();
        for (int i = 0; i < antAlgorithm.getMaxCities(); i++){
            series.getData().add(new XYChart.Data<>(cityTypes[antType.getPath()[i]].getX(), cityTypes[antType.getPath()[i]].getY()));
        }
        graph.setAxisSortingPolicy(SortingPolicy.NONE);
        graph.getData().add(series);
    }
}
