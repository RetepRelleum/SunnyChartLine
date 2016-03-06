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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import javafx.scene.paint.Color;

/**
 *
 * @author Peter
 */
public class DataViertelStd implements Comparable<DataViertelStd> {

    public static String KOPF_ZEILE = "Datum;Zeit;Leistung[kW];Energie[kWh]\n";

    private GregorianCalendar dateTime;
    private float leistung;

    public DataViertelStd(String datum, float leistung) {
        if (("".equals(datum)) || (datum == null)) {
            throw new NullPointerException("Datum ist null");
        }
        String[] strA = datum.split(":");

        int[] intA = new int[strA.length];
        for (int i = 0; i < strA.length; i++) {
            intA[i] = Integer.parseInt(strA[i]);
        }
        dateTime = new GregorianCalendar(intA[0], intA[1] - 1, intA[2], intA[3], intA[4]);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy:MM:dd:hh:mm");

        this.leistung = leistung;
    }

    public DataViertelStd(GregorianCalendar date, float leistung) {
        if (date == null) {
            throw new NullPointerException("Datum ist null");
        }
        dateTime = date;
        this.leistung = leistung;
    }

    /**
     * @return the dateTime
     */
    public GregorianCalendar getDateTime() {
        return dateTime;
    }

    /**
     * @param dateTime the dateTime to set
     */
    public void setDateTime(GregorianCalendar dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * @return the leistung
     */
    public float getLeistung() {
        return leistung;
    }

    public String getLeistungStr() {
        return String.format("%.3f", leistung);
    }

    /**
     * @param leistung the leistung to set
     */
    public void setLeistung(float leistung) {
        this.leistung = leistung;
    }

    public long getMillis() {
        return dateTime.getTimeInMillis();
    }

    @Override
    public int compareTo(DataViertelStd data) {
        return this.dateTime.compareTo(data.getDateTime());
    }

    @Override
    public String toString() {
        SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return dt.format(dateTime.getTime()) + " = " + leistung;
    }

    public String getFormatDatum() {
        SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy;HH:mm:ss");
        return dt.format(dateTime.getTime());
    }

    public String getTag() {
        SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy");
        return dt.format(dateTime.getTime());
    }

    public String getEnergie() {
        return String.format("%.3f", leistung / 4);
    }

    public String getZeit() {
        SimpleDateFormat dt = new SimpleDateFormat("HH:mm:ss");
        return dt.format(dateTime.getTime());
    }

    String getLine() {
        return getFormatDatum() + ";" + getLeistungStr() + ";" + getEnergie() + "\n";
    }

    public float getLeistung(long millis) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd");
        SimpleDateFormat fmtw = new SimpleDateFormat("u");
        int in = Integer.parseInt(fmt.format(new Date(millis)));
        int inw = Integer.parseInt(fmtw.format(new Date(millis)));

        if (in == 1 & leistung == 0) {
            return 0.2f;
        } else if (inw == 7 & leistung == 0) {
            return -0.2f;
        } else {
            return leistung;
        }

    }

    public Color getColor(float max) {
        float l = leistung;
        SimpleDateFormat fmt = new SimpleDateFormat("dd");
        SimpleDateFormat fmtw = new SimpleDateFormat("u");
        int in = Integer.parseInt(fmt.format(dateTime.getTime()));
        int inw = Integer.parseInt(fmtw.format(dateTime.getTime()));

        if (in == 1 & leistung == 0) {
            return Color.DIMGRAY;
        } else if (inw == 7 & leistung == 0) {
            return Color.DARKGRAY;
        } else if (leistung == 0) {
            return Color.LIGHTGRAY;
        } else {
            double d = leistung / max;
            d = d * 2;
            if (d < 1) {
                return Color.color(0,d, 1 - d);
            } else  {
                d=d-1;
                return Color.color(d, 1 - d, 0);
            }
            
        }
    }
}
