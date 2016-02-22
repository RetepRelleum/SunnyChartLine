/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sunnydata;

import javafx.scene.PerspectiveCamera;

/**
 *
 * @author Peter
 */
public class CameraXform extends Xform {

   private final PerspectiveCamera camera;
    private final Xform cameraXform2 = new Xform();
    private final Xform cameraXform3 = new Xform();
    private static final double CAMERA_INITIAL_DISTANCE = -450;
    private static final double CAMERA_INITIAL_X_ANGLE = 70.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
    private static final double CAMERA_NEAR_CLIP = 10.0;
    private static final double CAMERA_FAR_CLIP = 10000.0;

    public CameraXform(PerspectiveCamera camera) {
        this.camera=camera;
        System.out.println("buildCamera()");
        getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    }

    /**
     * @return the camera
     */
    public PerspectiveCamera getCamera() {
        return camera;
    }

    /**
     * @return the cameraXform2
     */
    public Xform getCameraXform2() {
        return cameraXform2;
    }

    /**
     * @return the cameraXform3
     */
    public Xform getCameraXform3() {
        return cameraXform3;
    }

    public void resetCameras() {
        t.setX(0.0);
        cameraXform2.t.setY(0.0);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    }
}
