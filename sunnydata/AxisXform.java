/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sunnydata;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

/**
 *
 * @author Peter
 */
public class AxisXform extends Xform{
      private  double axisLenght = 150.0;

    public AxisXform() {
        System.out.println("buildAxes()");
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        final Box xAxis = new Box(axisLenght, 10, 10);
        final Box yAxis = new Box(10, axisLenght, 10);
        final Box zAxis = new Box(10, 10, axisLenght);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        getChildren().addAll(xAxis, yAxis, zAxis);
        setVisible(true);
    
    }

    /**
     * @return the axisLenght
     */
    public double getAxisLenght() {
        return axisLenght;
    }

    /**
     * @param axisLenght the axisLenght to set
     */
    public void setAxisLenght(double axisLenght) {
        this.axisLenght = axisLenght;
    }
    
}
