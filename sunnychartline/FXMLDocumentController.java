/*
 * Copyright (C) 2016 Peter
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package sunnychartline;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.MeshView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import sunnydata.DataTag;
import sunnydata.DataTagVector;
import sunnydata.DataViertelStdVector;
import sunnydata.Prop;
import sunnydata.SunnyMesh;
import sunnydata.Xform;

/**
 *
 * @author Peter
 */
public class FXMLDocumentController implements Initializable {

    final Xform world = new Xform();
    private double scale = 0;
    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 2.0;
    private static final double TRACK_SPEED = 0.3;
    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;
    private DataTagVector dataTagVector;
    private DataViertelStdVector dataViertelStdVector;
    private final Prop prop = new Prop();
    private SunnyMesh sunnyMesh;

    @FXML
    LineChart<String, Double> chartLeistungId;
    @FXML
    LineChart<String, Double> chartEnergieId;
    @FXML
    LineChart<String, Double> chartZeitenId;
    @FXML
    Slider wertW25IdSlider;
    @FXML
    Label textIdW25;
    @FXML
    Label labelDirDataID;
    @FXML
    Label labelDirOutputID;
    @FXML
    TabPane tapPane;

    @FXML
    Pane pane;
    @FXML
    ListView listView;
    @FXML
    BorderPane bp;

    public FXMLDocumentController() {
        init();
    }

    private void init() {
        dataTagVector = new DataTagVector();
        for (File file : new File(prop.getDirData()).listFiles((File dir, String name) -> name.startsWith("Energie_und_Leistung_Tag"))) {
            float f = (float) (prop.getSliderValue());
            DataTag dataTag = new DataTag(file, f);
            dataTagVector.add(dataTag);

        }
        dataViertelStdVector = new DataViertelStdVector(dataTagVector);
        world.reset();
        world.getChildren().add(dataTagVector.getMesh().getXform());
    }

    public void initialize(URL url, ResourceBundle rb) {
        wertW25IdSlider.setValue(prop.getSliderValue());
        textIdW25.setText("Schwelle Eigenverbrauch " + wertW25IdSlider.getValue() + " kW");
        sunnyMesh = dataTagVector.getMesh();
        world.getChildren().add(sunnyMesh.getXform());

        pane.getChildren().add(world);
        pane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            onTab3d();
        });
        pane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            onTab3d();
        });
        long dd = dataTagVector.get(0).getMillis();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(dd);

        SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy");
        int lauf = 0;
        while (gregorianCalendar.getTimeInMillis() < System.currentTimeMillis()) {
            Label label = new Label();
            label.setText(dt.format(gregorianCalendar.getTime()));
            String s = dt.format(new Date(gregorianCalendar.getTimeInMillis()));

            if ((lauf < dataTagVector.size()) && (dataTagVector.get(lauf).getTag().equalsIgnoreCase(s))) {
                label.setStyle("-fx-background-color: #DCDCDC");
                lauf++;
            } else {
                label.setStyle("-fx-background-color: #FFD700");
            }
            listView.getItems().add(label);
            gregorianCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

    }

    public void onSliderDrag() {
        double silderValue;
        silderValue = wertW25IdSlider.getValue();
        silderValue = Math.floor(silderValue * 10) / 10;
        textIdW25.setText("Schwelle Eigenverbrauch " + silderValue + " kW");
        onTabZeit();
        prop.setSliderValue(silderValue);
    }

    public void getFileDirData() {
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setInitialDirectory(new File(prop.getDirData()));
        fileChooser.setTitle("CSV File Input");
        File selectedFile = fileChooser.showDialog(null);
        if (selectedFile != null) {
            prop.setDirData(selectedFile.getPath());
        }
        for (File file : new File(prop.getDirData()).listFiles()) {
            dataTagVector.add(new DataTag(file, (float) prop.getSliderValue()));
        }
        dataViertelStdVector = new DataViertelStdVector(dataTagVector);
        labelDirDataID.setText(prop.getDirData());
        init();
    }

    public void getFileDirOutput() {
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setInitialDirectory(new File(prop.getDirOutput()));
        fileChooser.setTitle("CSV File Output");
        File selectedFile = fileChooser.showDialog(null);
        if (selectedFile != null) {
            prop.setDirOutput(selectedFile.getPath());
        }
        labelDirOutputID.setText(prop.getDirOutput());
    }

    public void onTabLeistung() {
        chartLeistungId.getData().clear();
        chartLeistungId.getData().add(dataTagVector.getLeistung());
        chartLeistungId.setCreateSymbols(false);
    }

    public void onTabEnergie() {
        chartEnergieId.getData().clear();
        chartEnergieId.getData().add(dataTagVector.getEnergie());
        chartEnergieId.setCreateSymbols(false);
    }

    public void onTabZeit() {
        chartZeitenId.getData().clear();
        chartZeitenId.getData().add(dataTagVector.getWrStart());
        chartZeitenId.getData().add(dataTagVector.getWrEnd());
        chartZeitenId.getData().add(dataTagVector.getSonnenAufgang());
        chartZeitenId.getData().add(dataTagVector.getSonnenUntergang());
        chartZeitenId.getData().add(dataTagVector.getW25Start((float) wertW25IdSlider.getValue()));
        chartZeitenId.getData().add(dataTagVector.getW25End((float) wertW25IdSlider.getValue()));
        chartZeitenId.getData().add(dataTagVector.getEigDauer((float) wertW25IdSlider.getValue()));
        // chartZeitenId.setCreateSymbols(false);
    }

    public void onTabEigenschaften() {
        labelDirDataID.setText(prop.getDirData());
        labelDirOutputID.setText(prop.getDirOutput());
    }

    public void setOnMousePressed(MouseEvent me) {
        mousePosX = me.getSceneX();
        mousePosY = me.getSceneY();
        mouseOldX = me.getSceneX();
        mouseOldY = me.getSceneY();
    }

    public void setOnMouseDragged(MouseEvent me) {
        mouseOldX = mousePosX;
        mouseOldY = mousePosY;
        mousePosX = me.getSceneX();
        mousePosY = me.getSceneY();
        mouseDeltaX = (mousePosX - mouseOldX);
        mouseDeltaY = (mousePosY - mouseOldY);

        double modifier = 4.0;

        if (me.isControlDown()) {
            modifier = CONTROL_MULTIPLIER;
        }
        if (me.isShiftDown()) {
            modifier = SHIFT_MULTIPLIER;
        }
        if (me.isPrimaryButtonDown()) {
            world.ry.setAngle(world.ry.getAngle() - mouseDeltaX * MOUSE_SPEED * modifier * ROTATION_SPEED);
            world.rx.setAngle(world.rx.getAngle() + mouseDeltaY * MOUSE_SPEED * modifier * ROTATION_SPEED);
        } else if (me.isSecondaryButtonDown()) {
            double z = world.getTranslateZ();
            double newZ = z + mouseDeltaX * MOUSE_SPEED * modifier;
            world.setTranslateZ(newZ);
        } else if (me.isMiddleButtonDown()) {
            world.t.setX(world.t.getX() + mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);
            world.t.setY(world.t.getY() + mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
        }
    }

    public void setOnKeyPressed(KeyEvent event) {

        switch (event.getCode()) {
            case Z:
                world.reset();
                break;

        }

    }

    public void onTab3d() {

        world.setRotate(180, 20, 45);

        double sx = pane.getWidth() / world.getWidth();
        double sy = pane.getHeight() / world.getHeight();
        scale = (sx < sy ? sx : sy) * 0.5;
        world.setScale(scale);
        world.setTranslate(pane.getWidth() / 2, pane.getHeight() / 2);
    }

    public void setOnMouseScroll(ScrollEvent me) {
        scale = scale + 0.1 * Math.signum(me.getDeltaY());
        world.setScale(scale);
        tapPane.autosize();
    }

    public void setOnMouseReleased() {
        tapPane.autosize();
    }

    public void export() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export");
        fileChooser.setInitialDirectory(new File(prop.getDirOutput()));
        fileChooser.setInitialFileName("CSV");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Export Csv File", ".csv"),
                new FileChooser.ExtensionFilter("Export 3D OBJ File", ".obj"),
                new FileChooser.ExtensionFilter("STL File", ".stl"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            if (file.getName().endsWith("csv")) {
                dataTagVector.writeCsv(file.getPath());
            } else if (file.getName().endsWith("obj")) {
                sunnyMesh.mesh2OBJ(file);
            } else if (file.getName().endsWith("stl")) {
                sunnyMesh.mesh2STL(file.getPath());
            }
        }
    }

    public void print() {
        PrinterJob job = PrinterJob.createPrinterJob();
        Node node;
        boolean success = false;
        if (job != null) {
            if (job.showPrintDialog(null) == false) {
                job.endJob();
                return;
            }
            switch (tapPane.getSelectionModel().getSelectedIndex()) {
                case 0:
                    chartLeistungId.getXAxis().setVisible(true);
                    chartLeistungId.getYAxis().setVisible(true);
                    chartLeistungId.setLegendVisible(true);
                    node = chartLeistungId.getParent();
                    node.autosize();
                    success = job.printPage(node);
                    break;
                case 1:
                    chartEnergieId.getXAxis().setVisible(true);
                    chartEnergieId.getYAxis().setVisible(true);
                    chartEnergieId.setLegendVisible(true);
                    node = chartEnergieId.getParent();
                    node.autosize();
                    success = job.printPage(node);
                    break;
                case 2:
                    chartZeitenId.getXAxis().setVisible(true);
                    chartZeitenId.getYAxis().setVisible(true);
                    chartZeitenId.setLegendVisible(true);
                    node = chartZeitenId.getParent();
                    node.autosize();
                    success = job.printPage(node);
                    break;
                case 3:
                    MeshView meshView = sunnyMesh.getMesh();
                    meshView.setVisible(true);
                    success = job.printPage(meshView);
                    break;
            }

            if (success) {
                job.endJob();
            }
        }
    }
}
