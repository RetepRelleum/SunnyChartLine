/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sunnydata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import sunnychartline.FXMLDocumentController;

/**
 *
 * @author Peter
 */
public class Prop extends Properties {

    public Prop() {
        loadParams();
    }

    public String getDirData() {
        return getProperty("dirData", "C:\\SunnyData\\data");
    }

    public void setDirData(String text) {
        setProperty("dirData", text);
        saveParamChanges();
    }

    public String getDirOutput() {
        return getProperty("dirOutput", "C:\\SunnyData\\");
    }

    public void setDirOutput(String text) {
        setProperty("dirOutput", text);
        saveParamChanges();
    }

    public void saveParamChanges() {
        OutputStream out = null;
        try {
            File f = new File("properties.prop");
            out = new FileOutputStream(f);
            store(out, "This is an optional header comment string");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Properties.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Properties.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(Properties.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void loadParams() {
        InputStream is = null;
        try {
            File f = new File("properties.prop");
            is = new FileInputStream(f);
            load(is);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            is = getClass().getResourceAsStream("server.properties");
        } catch (IOException ex) {
            Logger.getLogger(Prop.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public double getSliderValue() {
         return Double.parseDouble(getProperty("sliderValue", "2.5"));
    }
    
    public void setSliderValue(double value) {
           setProperty("sliderValue",""+value);
        saveParamChanges();
        
       
       }

}
