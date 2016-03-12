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

import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.chart.LineChart;

/**
 *
 * @author Peter
 */
public class DataTagVector extends Vector<DataTag> {

    private float max = 0;

    public void writeCsv(String dir) {
        FileWriter writerSummen = null;
        FileWriter writer = null;
        try {
            writerSummen = new FileWriter(dir.replace(".csv", "summe.csv"));
            writer = new FileWriter(dir);
            writerSummen.append(DataTag.KOPF_ZEILE);
            writer.write(DataViertelStd.KOPF_ZEILE);
            for (DataTag da : this) {
                writerSummen.append(da.getLine());
                for (DataViertelStd dai : da) {
                    writer.append(dai.getLine());
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(DataTagVector.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {

                writerSummen.flush();
                writerSummen.close();
                writer.flush();
                writer.close();

            } catch (IOException ex) {
                Logger.getLogger(DataTagVector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public LineChart.Series<String, Double> getLeistung() {
        LineChart.Series<String, Double> series = new LineChart.Series<>();
        series.setName("Max Leistung");
        this.stream().forEach((data) -> {
            if (data.getMax() != 0) {
                series.getData().add(new LineChart.Data<>(data.getTag(), (double) data.getMax()));
            }
        });
        return series;
    }

    public LineChart.Series<String, Double> getEnergie() {
        LineChart.Series<String, Double> series = new LineChart.Series<>();
        series.setName("Energie");
        this.stream().forEach((data) -> {
            if (data.getEnergie() != 0) {
                series.getData().add(new LineChart.Data<>(data.getTag(), data.getEnergie()));
            }
        });
        return series;
    }

    public LineChart.Series<String, Double> getWrStart() {
        LineChart.Series<String, Double> series = new LineChart.Series<>();
        series.setName("We_ri Start");
        this.stream().forEach((data) -> {
            if (data.getWrStartD() != 0) {
                series.getData().add(new LineChart.Data<>(data.getTag(), data.getWrStartD()));
            }
        });
        return series;
    }

    public LineChart.Series<String, Double> getWrEnd() {
        LineChart.Series<String, Double> series = new LineChart.Series<>();
        series.setName("We_ri End");
        this.stream().forEach((data) -> {
            if (data.getWrEndD() != 0) {
                series.getData().add(new LineChart.Data<>(data.getTag(), data.getWrEndD()));
            }
        });
        return series;
    }

    public LineChart.Series<String, Double> getSonnenAufgang() {
        LineChart.Series<String, Double> series = new LineChart.Series<>();
        series.setName("Sonnenaufgang");
        this.stream().forEach((data) -> {
            if (data.getSonnenAufgangD() != 0) {
                series.getData().add(new LineChart.Data<>(data.getTag(), data.getSonnenAufgangD()));
            }
        });
        return series;
    }

    public LineChart.Series<String, Double> getSonnenUntergang() {
        LineChart.Series<String, Double> series = new LineChart.Series<>();
        series.setName("Sonnenuntergang");
        this.stream().forEach((data) -> {
            if (data.getSonnenUntergangD() != 0) {
                series.getData().add(new LineChart.Data<>(data.getTag(), data.getSonnenUntergangD()));
            }
        });
        return series;
    }

    public LineChart.Series<String, Double> getW25Start(float w25) {
        LineChart.Series<String, Double> series = new LineChart.Series<>();
        series.setName("Eigenverbr. Start");
        this.stream().forEach((data) -> {
            if (data.getW25StartD(w25) != 0) {
                series.getData().add(new LineChart.Data<>(data.getTag(), data.getW25StartD(w25)));
            }
        });
        return series;
    }

    public LineChart.Series<String, Double> getW25End(float w25) {
        LineChart.Series<String, Double> series = new LineChart.Series<>();
        series.setName("Eigenverbr. End");
        this.stream().forEach((data) -> {
            if (data.getW25EndD(w25) != 0) {
                series.getData().add(new LineChart.Data<>(data.getTag(), data.getW25EndD(w25)));
            }
        });
        return series;
    }

    public LineChart.Series<String, Double> getEigDauer(float w25) {
        LineChart.Series<String, Double> series = new LineChart.Series<>();
        series.setName("Eigenverbr. Dauer");
        this.stream().forEach((data) -> {
            if ((data.getW25EndD(w25) != 0) && (data.getW25StartD(w25) != 0)) {
                series.getData().add(new LineChart.Data<>(data.getTag(), data.getW25EndD(w25) - data.getW25StartD(w25)));
            }
        });
        return series;
    }

    public SunnyMesh getMesh() {
        SunnyMesh sunnyMesh = new SunnyMesh(this);
        return sunnyMesh;
    }

    /**
     * @return the max
     */
    public float getMax() {
        return max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(float max) {
        this.max = max;
    }

    @Override
    public synchronized boolean add(DataTag e) {
        max = max < e.getMax() ? e.getMax() : max;
        return super.add(e);
    }

    public boolean contains(String st) {
        return true;
    }

}
