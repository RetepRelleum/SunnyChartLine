/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sunnydata;

import java.util.Vector;

/**
 *
 * @author Peter
 */
public class DataViertelStdVector extends Vector<DataViertelStd> {

  

    DataTagVector dataTagVector;

    public DataViertelStdVector(DataTagVector dataTagVector) {
        this.dataTagVector= dataTagVector;
    
    }
}
