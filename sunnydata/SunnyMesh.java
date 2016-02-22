/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sunnydata;

import java.awt.image.RenderedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableFloatArray;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point3D;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.ObservableFaceArray;
import javafx.scene.shape.TriangleMesh;
import javax.imageio.ImageIO;

/**
 *
 * @author Peter
 */
public class SunnyMesh extends TriangleMesh {

    WritableImage imageW = null;
    float mx = 0, my = 0, mz = 0;

    public SunnyMesh(DataTagVector dataTagVector) {
        if (dataTagVector.isEmpty()) {
            return;
        }
        dataTagVector.stream().forEach((dataTag) -> {
            addMesh(dataTag, dataTagVector.getMax());
        });
        if (dataTagVector.isEmpty()) {
            return;
        }
        getFaces().addAll(dataTagVector.get(0).getFirstFaces());
        getFaces().addAll(dataTagVector.get(0).getLastFaces(getPoints().size()));

        for (int i = 1; i < getFaces().size(); i = i + 2) {
            getFaces().set(i, getFaces().get(i - 1));
        }

        for (int i = 0; i < getPoints().size(); i++) {
            switch ((i % 3)) {
                case 0:
                    mx = mx > getPoints().get(i) ? mx : getPoints().get(i);
                    break;
                case 1:
                    my = my > getPoints().get(i) ? my : getPoints().get(i);
                    break;
                case 2:
                    mz = mz > getPoints().get(i) ? mz : getPoints().get(i);
                    break;

            }
        }

        for (int i = 0; i < getPoints().size(); i++) {

            switch ((i % 3)) {
                case 0:
                    float f = getPoints().get(i) / mx;               
                    getTexCoords().addAll(f);
                    break;
                case 1:
                    getTexCoords().addAll(getPoints().get(i) / my);
                    break;
                case 2:

                    break;
            }
        }
    }

    public void mesh2STL(String fileName) {
        convertSTL(fileName);

    }

    public void mesh2OBJ(File file) {
        convertOBJ(file);
    }

    private void convertSTL(String fileName) {
        if (!(this instanceof TriangleMesh)) {
            return;
        }
        // Get faces
        ObservableFaceArray faces = ((TriangleMesh) this).getFaces();
        int[] f = new int[faces.size()];
        faces.toArray(f);
        // Get vertices
        ObservableFloatArray points = ((TriangleMesh) this).getPoints();
        float[] p = new float[points.size()];
        points.toArray(p);
        StringBuilder sb = new StringBuilder();
        sb.append("solid meshFX\n");
        // convert faces to polygons
        for (int i = 0; i < faces.size() / 6; i++) {
            int i0 = f[6 * i], i1 = f[6 * i + 2], i2 = f[6 * i + 4];
            Point3D pA = new Point3D(p[3 * i0], p[3 * i0 + 1], p[3 * i0 + 2]);
            Point3D pB = new Point3D(p[3 * i1], p[3 * i1 + 1], p[3 * i1 + 2]);
            Point3D pC = new Point3D(p[3 * i2], p[3 * i2 + 1], p[3 * i2 + 2]);
            Point3D pN = pB.subtract(pA).crossProduct(pC.subtract(pA)).normalize();

            sb.append("  facet normal ").append(pN.getX()).append(" ").append(pN.getY()).append(" ").append(pN.getZ()).append("\n");
            sb.append("    outer loop\n");
            sb.append("      vertex ").append(pA.getX()).append(" ").append(pA.getY()).append(" ").append(pA.getZ()).append("\n");
            sb.append("      vertex ").append(pB.getX()).append(" ").append(pB.getY()).append(" ").append(pB.getZ()).append("\n");
            sb.append("      vertex ").append(pC.getX()).append(" ").append(pC.getY()).append(" ").append(pC.getZ()).append("\n");
            sb.append("    endloop\n");
            sb.append("  endfacet\n");
        }
        sb.append("endsolid meshFX\n");
        // write file
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName), Charset.forName("UTF-8"),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(sb.toString());
        } catch (IOException ex) {
            Logger.getLogger(SunnyMesh.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void convertOBJ(File file) {
        if (!(this instanceof TriangleMesh)) {
            return;
        }
        // Get faces
        ObservableFaceArray faces = ((TriangleMesh) this).getFaces();
        int[] f = new int[faces.size()];
        faces.toArray(f);
        ObservableFloatArray texc = ((TriangleMesh) this).getTexCoords();
        float[] t = new float[texc.size()];
        texc.toArray(t);
        // Get vertices
        ObservableFloatArray points = ((TriangleMesh) this).getPoints();
        float[] p = new float[points.size()];
        points.toArray(p);
        StringBuilder sb = new StringBuilder();
        StringBuilder sbmtl = new StringBuilder();
        sb.append("# Retep Relleum \n");
        sb.append("mtllib ").append(file.getName().replace(".obj", "")).append(".mtl \n");
        sb.append("o SunnyPvGraph \n");
        // convert faces to polygons
        for (int i = 0; i < p.length; i++) {
            switch ((i % 3)) {
                case 0:
                    sb.append("v ").append(p[i]);
                    break;
                case 1:
                    sb.append(" ").append(p[i]);
                    break;
                case 2:
                    sb.append(" ").append(p[i]).append(" 1 1 1 \n");
                    break;
            }

        }
        for (int i = 0; i < t.length; i++) {
            switch ((i % 2)) {
                case 0:
                    sb.append("vt ").append(t[i]);
                    break;
                case 1:
                    sb.append(" ").append(t[i]).append("  \n");
                    break;
            }

        }
        sb.append("usemtl ").append(file.getName().replace(".obj", "")).append("_Texture_0\n");
        for (int i = 0; i < f.length; i++) {
            switch ((i % 6)) {
                case 0:
                    sb.append("f ").append(f[i] + 1);
                    break;
                case 1:
                    sb.append("/").append(f[i] + 1);
                    break;
                case 2:
                    sb.append(" ").append(f[i] + 1);
                    break;
                case 3:
                    sb.append("/").append(f[i] + 1);
                    break;
                case 4:
                    sb.append(" ").append(f[i] + 1);
                    break;
                case 5:
                    sb.append("/").append(f[i] + 1).append("\n");
                    break;
            }

        }

        sbmtl.append("newmtl ").append(file.getName().replace(".obj", "")).append("_Texture_0\n");
        sbmtl.append("Ka 1.000000 1.000000 1.000000").append("\n");
        sbmtl.append("Ks 0.000000 0.000000 0.000000").append("\n");
        sbmtl.append("d 1").append("\n");
        sbmtl.append("Ns 0.00000").append("\n");
        sbmtl.append("illum 1").append("\n");
        sbmtl.append("map_Kd ").append(file.getName().replace(".obj", "")).append("_Texture_0.png").append("\n");
        // write file
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), Charset.forName("UTF-8"),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(sb.toString());
        } catch (IOException ex) {
            Logger.getLogger(SunnyMesh.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.getPath().replace(".obj", "") + ".mtl"), Charset.forName("UTF-8"),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(sbmtl.toString());
        } catch (IOException ex) {
            Logger.getLogger(SunnyMesh.class.getName()).log(Level.SEVERE, null, ex);
        }
        WritableImage wr = new WritableImage((int) imageW.getWidth(), (int) imageW.getHeight());
        PixelReader pixelReader = imageW.getPixelReader();
        PixelWriter pixelWriter = wr.getPixelWriter();
        for (int x = 0; x < imageW.getWidth(); x++) {
            for (int y = 0; y < imageW.getHeight(); y++) {
                pixelWriter.setColor(x, y, pixelReader.getColor(x, (int) imageW.getHeight() - y - 1));
            }
        }
        File fileobj = new File("" + file.getPath().replace(".obj", "") + "_Texture_0.png");
        RenderedImage renderedImage = SwingFXUtils.fromFXImage(wr, null);
        try {
            ImageIO.write(renderedImage, "png", fileobj);
        } catch (IOException ex) {
            Logger.getLogger(SunnyMesh.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void addMesh(DataTag dataTag, float max) {
        float ddx = 0.5f;
        float dPoints = 0;
        int dFace = 0;
        if (getPoints().size() > 3) {
            dPoints = getPoints().get(getPoints().size() - 3);
            dFace = getPoints().size() / 3;
        }
        int count = 1;
        for (float f : dataTag.getPoints()) {
            if (count % 3 == 1) {
                getPoints().addAll(f + dPoints + ((1 - dataTag.getDx()) * ddx));
            } else {
                getPoints().addAll(f);
            }
            count++;
        }

        boolean b = true;
        for (int f : dataTag.getFaces()) {
            if (b) {
                getFaces().addAll(f + dFace);
            } else {
                getFaces().addAll(f);
            }
            b = !b;
        }

        for (int f : dataTag.getFaces(getPoints().size())) {
            if (b) {
                getFaces().addAll(f);
            } else {
                getFaces().addAll(f);
            }
            b = !b;
        }

        if (imageW == null) {
            imageW = dataTag.getColors(max);
        } else {
            WritableImage writableImage = new WritableImage((int) (imageW.getWidth() + dataTag.getWidth()), (int) imageW.getHeight());
            PixelWriter pixelWriter = writableImage.getPixelWriter();
            PixelReader pixelReader = imageW.getPixelReader();
            for (int x = 0; x < imageW.getWidth(); x++) {
                for (int y = 0; y < imageW.getHeight(); y++) {
                    pixelWriter.setColor(x, y, pixelReader.getColor(x, y));
                }
            }
            pixelReader = dataTag.getColors(max).getPixelReader();
            for (int x = 0; x < dataTag.getWidth(); x++) {
                for (int y = 0; y < dataTag.getHeight(); y++) {
                    pixelWriter.setColor((int) (x + imageW.getWidth()), (int) (y), pixelReader.getColor(x, y));
                }
            }
            imageW = writableImage;
        }

    }

    public Xform getXform() {
        Xform xform = new Xform();
        MeshView meshView = new MeshView(this);
        meshView.setMaterial(new PhongMaterial(Color.LIGHTYELLOW, imageW, imageW, null, null));
        meshView.setTranslateX(-mx / 2);
        meshView.setTranslateY(-my / 2);
        meshView.setTranslateZ(-mz / 2);
        xform.getChildren().addAll(meshView);
        xform.setVisible(true);
        return xform;
    }

    public MeshView getMesh() {
        MeshView meshView = new MeshView(this);

        return meshView;
    }

}
