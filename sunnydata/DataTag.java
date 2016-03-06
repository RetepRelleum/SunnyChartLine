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

package sunnydata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 *
 * @author Peter
 */
public final class DataTag extends Vector<DataViertelStd> {

    public static String KOPF_ZEILE = "Tag;Einschaltzeit;Ausschaltzeit;Aufg.Sonne;Unter.Sonne;G2,5;K2,5;max Leistung;Energie\n";
    private String wrStart = "";
    private String wrEnd = "";
    private String sonnenAufgang = "";
    private String sonnenUntergang;
    private float max = 0;
    private double energie = 0;
    private float w25 = 2.5f;
    private float dx = 0.5f;
    private int height =1;

    public DataTag(File file, float wertW25) {

        this.w25 = wertW25;
        String line;
        String[] strArr;
        BufferedReader bufferedReader;
        try {
            if (file.getName().endsWith("csv")) {
                String datumStr = file.getName().replaceFirst("Energie_und_Leistung_Tag_", "").replaceAll(".csv", "").replaceAll("-", ":");
                bufferedReader = new BufferedReader(new FileReader(file));
                while ((line = bufferedReader.readLine()) != null) {
                    strArr = line.split(";");
                    if (strArr[0].contains(":") && (strArr.length > 1)) {
                        add(new DataViertelStd(datumStr + ":" + strArr[0], Float.parseFloat(strArr[1].replace(",", "."))));
                    }
                }
            } else {

            }
        } catch (IOException ex) {
            Logger.getLogger(DataViertelStdVector.class.getName()).log(Level.SEVERE, null, ex);
        }
        Collections.sort(this);

        calculate();

    }

    public void calculate() {
        boolean startBool = true;
        boolean sunABool = true;
       

        for (DataViertelStd da : this) {

            if (startBool) {
                if (da.getLeistung() != 0) {
                    setWrStart(da.getZeit());
                    startBool = false;
                }
            }
            if (sunABool) {
                if (da.getLeistung() > 1) {
                    setSonnenAufgang(da.getZeit());
                    sunABool = false;
                }
            }

            if (da.getLeistung() > 0) {
                setWrEnd(da.getZeit());
            }
            if (da.getLeistung() > 1) {
                setSonnenUntergang(da.getZeit());
            }

            if (getMax() < da.getLeistung()) {
                setMax(da.getLeistung());
            }
            setEnergie(getEnergie() + da.getLeistung() / 4);
        }
    }

    /**
     * @return the wrStart
     */
    public String getWrStart() {
        return wrStart;
    }

    public double getWrStartD() {
        return StringToDouble(wrStart);
    }

    /**
     * @param wrStart the wrStart to set
     */
    private void setWrStart(String wrStart) {
        this.wrStart = wrStart;
    }

    public double getWrEndD() {
        return StringToDouble(wrEnd);
    }

    /**
     * @return the wrEnd
     */
    public String getWrEnd() {
        return wrEnd;
    }

    /**
     * @param wrEnd the wrEnd to set
     */
    private void setWrEnd(String wrEnd) {
        this.wrEnd = wrEnd;
    }

    /**
     * @return the sonnenAufgang
     */
    public String getSonnenAufgang() {
        return sonnenAufgang;
    }

    public double getSonnenAufgangD() {
        return StringToDouble(sonnenAufgang);
    }

    public double getSonnenUntergangD() {
        return StringToDouble(sonnenUntergang);
    }

    /**
     * @param sonnenAufgang the sonnenAufgang to set
     */
    private void setSonnenAufgang(String sonnenAufgang) {
        this.sonnenAufgang = sonnenAufgang;
    }

    /**
     * @return the sonnenUntergang
     */
    public String getSonnenUntergang() {
        return sonnenUntergang;
    }

    /**
     * @param sonnenUntergang the sonnenUntergang to set
     */
    private void setSonnenUntergang(String sonnenUntergang) {
        this.sonnenUntergang = sonnenUntergang;
    }

    /**
     * @param w25
     * @return the w25Start
     */
    public String getW25End(float w25) {
        this.w25 = w25;
        String w25End = "";
        for (DataViertelStd da : this) {
            if (da.getLeistung() > w25) {
                w25End = da.getZeit();
            }
        }
        return w25End;
    }

    public double getW25StartD(float w25) {
        return StringToDouble(getW25Start(w25));
    }

    /**
     * @param w25
     * @return the w25End
     */
    public String getW25Start(float w25) {
        this.w25 = w25;
        for (DataViertelStd da : this) {
            if (da.getLeistung() > w25) {
                return da.getZeit();
            }
        }
        return "";
    }

    public double getW25EndD(float w25) {
        return StringToDouble(getW25End(w25));
    }

    private void setMax(float max) {
        this.max = max;
    }

    public float getMax() {
        return max;
    }

    public double getEnergie() {

        return energie;
    }

    private void setEnergie(double energie) {
        this.energie = energie;
    }

    public String getTag() {
        return firstElement().getTag();
    }

    public CharSequence getLine() {
        return getTag() + ";" + wrStart + ";" + wrEnd + ";" + sonnenAufgang + ";" + sonnenUntergang + ";" + getW25Start(w25) + ";" + getW25End(w25) + ";" + String.format("%.3f", max) + ";" + String.format("%.3f", energie) + "\n";
    }

    public long getMillis() {
        return firstElement().getMillis();
    }

    private double StringToDouble(String str) {
        if ((str == null) || (str.equals(""))) {
            return 0;
        }
        String[] sA = str.split(":");
        return Double.parseDouble(sA[0]) + Double.parseDouble(sA[1]) / 60;
    }

    public WritableImage getColors(float max) {
        int count = 0;
        WritableImage image = new WritableImage(height, size());
        PixelWriter writer = image.getPixelWriter();
        for (DataViertelStd dataViertelStd : this) {
            writer.setColor(0, count, dataViertelStd.getColor(max));
            count++;
        }
        
        return image;
    }

    public float[] getPoints() {
        float ddx=0.5f;
        Vector<Float> flA = new Vector<>();
        int i = 0;
        
        for (DataViertelStd dataViertelStd : this) {
            flA.add(0f);
            flA.add((float) ((float) i++ / size() * 24.0));
            flA.add(-1f);
            flA.add(0f);
            flA.add((float) ((float) i++ / size() * 24.0));
            flA.add(-1f);
        }

        i = 0;
        for (DataViertelStd dataViertelStd : this) {
            flA.add(0f);
            flA.add((float) ((float) i++ / (float) size() * 24.0));
            flA.add(dataViertelStd.getLeistung(getMillis()));
            flA.add(0f);
            flA.add((float) ((float) i++ / (float) size() * 24.0));
            flA.add(dataViertelStd.getLeistung(getMillis()));

        }
        i = 0;
        for (DataViertelStd dataViertelStd : this) {
            flA.add(getDx()*ddx);
            flA.add((float) ((float) i++ / (float) size() * 24.0));
            flA.add(dataViertelStd.getLeistung(getMillis()));
            flA.add(getDx()*ddx);
            flA.add((float) ((float) i++ / (float) size() * 24.0));
            flA.add(dataViertelStd.getLeistung(getMillis()));

        }
        i = 0;
        for (DataViertelStd dataViertelStd : this) {
            flA.add(getDx()*ddx);
            flA.add((float) ((float) i++ / (float) size() * 24.0));
            flA.add(-1f);
            flA.add(getDx()*ddx);
            flA.add((float) ((float) i++ / (float) size() * 24.0));
            flA.add(-1f);

        }
        float[] fA = new float[flA.size()];
        int count = 0;
        for (float fi : flA) {
            fA[count++] = fi;
        }
        return fA;
    }

    /**
     *
     * @return
     */
    public int[] getFaces() {
        Vector<Integer> flA = new Vector<>();
        for (int i = this.size() * 2; i < this.size() * 4; i++) {
            if ((i % ((this.size() * 2))) != (this.size() * 2 - 1)) {
                flA.add(i);
                flA.add(0);//
                flA.add(i + (this.size() * 2));
                flA.add(0);//
                flA.add(i + (this.size() * 2) + 1);
                flA.add(0);
                flA.add(i);
                flA.add(0);
                flA.add(i + (this.size() * 2) + 1);
                flA.add(0);
                flA.add(i + 1);
                flA.add(0);
            }
        }

        flA.add(0);
        flA.add(0);
        flA.add((this.size() * 4));
        flA.add(0);
        flA.add((this.size() * 2));
        flA.add(0);

        flA.add(0);
        flA.add(0);
        flA.add((this.size() * 6));
        flA.add(0);
        flA.add((this.size() * 4));
        flA.add(0);

        flA.add(this.size() * 2 - 1);
        flA.add(0);
        flA.add((this.size() * 4) - 1);
        flA.add(0);
        flA.add((this.size() * 6) - 1);
        flA.add(0);

        flA.add((this.size() * 2) - 1);
        flA.add(0);
        flA.add((this.size() * 6) - 1);
        flA.add(0);
        flA.add((this.size() * 8) - 1);
        flA.add(0);

        flA.add(0);
        flA.add(0);
        flA.add((this.size() * 2) - 1);
        flA.add(0);
        flA.add((this.size() * 6));
        flA.add(0);

        flA.add((this.size() * 6));
        flA.add(0);
        flA.add((this.size() * 2) - 1);
        flA.add(0);
        flA.add((this.size() * 8) - 1);
        flA.add(0);

        int[] fA = new int[flA.size()];
        int count = 0;
        for (int fi : flA) {
            fA[count++] = fi;
        }
        return fA;
    }

    public int[] getFaces(int size) {

        int dx = size / 3 - this.size() * 14;
        if (dx < 1) {
            return new int[0];
        }
        Vector<Integer> flA = new Vector<>();
        for (int i = this.size() * 2 + dx; i < this.size() * 4 + dx; i++) {
            if ((i % ((this.size() * 2))) != (this.size() * 2 - 1)) {
                flA.add(i);
                flA.add(0);
                
                flA.add(i + (this.size() * 6));              
                flA.add(0);
                
                flA.add(i + (this.size() * 6) + 1);
                flA.add(0);
                
                flA.add(i);
                flA.add(0);
                
                flA.add(i + (this.size() * 6) + 1);               
                flA.add(0);
                
                flA.add(i + 1);
                flA.add(0);
            }
        }

        flA.add(this.size() * 4 + dx);
        flA.add(0);
        flA.add((this.size() * 8) + dx);
        flA.add(0);
        flA.add((this.size() * 2) + dx);
        flA.add(0);

        flA.add(this.size() * 4 + dx);
        flA.add(0);
        flA.add((this.size() * 6) + dx);
        flA.add(0);
        flA.add((this.size() * 8) + dx);
        flA.add(0);

        flA.add(this.size() * 8 - 1 + dx);
        flA.add(0);
        flA.add(this.size() * 4 - 1 + dx);
        flA.add(0);
        flA.add(this.size() * 10 - 1 + dx);
        flA.add(0);

        flA.add((this.size() * 8) - 1 + dx);
        flA.add(0);
        flA.add((this.size() * 6) - 1 + dx);
        flA.add(0);
        flA.add((this.size() * 4) - 1 + dx);
        flA.add(0);

        flA.add(this.size() * 8 + dx - 1);
        flA.add(0);
        flA.add(this.size() * 6 + dx);
        flA.add(0);
        flA.add(this.size() * 6 + dx - 1);
        flA.add(0);

        flA.add(this.size() * 6 + dx);
        flA.add(0);
        flA.add(this.size() * 4 + dx);
        flA.add(0);
        flA.add(this.size() * 6 + dx - 1);
        flA.add(0);

        int[] fA = new int[flA.size()];
        int count = 0;
        for (int fi : flA) {
            fA[count++] = fi;
        }

        return fA;
    }

    public int[] getFirstFaces() {
        Vector<Integer> flA = new Vector<>();
        for (int i = 0; i < this.size() * 2; i++) {
            if ((i % ((this.size() * 2))) != (this.size() * 2 - 1)) {
                flA.add(i);
                flA.add(0);
                flA.add(i + (this.size() * 2));
                flA.add(0);
                flA.add(i + (this.size() * 2) + 1);
                flA.add(0);
                flA.add(i);
                flA.add(0);
                flA.add(i + (this.size() * 2) + 1);
                flA.add(0);
                flA.add(i + 1);
                flA.add(0);
            }
        }
        int[] fA = new int[flA.size()];
        int count = 0;
        for (int fi : flA) {
            fA[count++] = fi;
        }
        return fA;
    }

    int[] getLastFaces(int size) {
        
        int dx = size / 3 - size() * 4;
        if (dx < 1) {
            return new int[0];
        }
        Vector<Integer> flA = new Vector<>();
        for (int i = 0; i < this.size() * 2; i++) {
            if ((i % ((this.size() * 2))) != (this.size() * 2 - 1)) {
                flA.add(i + dx);
                flA.add(0);
                flA.add(i + (this.size() * 2) + dx);
                flA.add(0);
                flA.add(i + (this.size() * 2) + 1 + dx);
                flA.add(0);
                flA.add(i + dx);
                flA.add(0);
                flA.add(i + (this.size() * 2) + 1 + dx);
                flA.add(0);
                flA.add(i + 1 + dx);
                flA.add(0);
            }
        }
        int[] fA = new int[flA.size()];
        int count = 0;
        for (int fi : flA) {
            fA[count++] = fi;
        }
        return fA;
    }

    /**
     * @return the dx
     */
    public float getDx() {
        return dx;
    }

    /**
     * @param dx the dx to set
     */
    public void setDx(float dx) {
        this.dx = dx;
    }

    public int getWidth() {
        return height;
    }

    public int getHeight() {
       return size();
    }

}
