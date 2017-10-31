package centralvrswiss;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;

//import vrml.*;
//import vrml.field.*;
//import vrml.node.*;

/**
 * 
 * @author Nathan
 */
public class CentralVRSwiss {
    
    private final static String FILE_NAME = "points/53394620_dsm_1m.dat";
    private static int nbPoints;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        CentralVRSwiss cvrs = new CentralVRSwiss();
        
        cvrs.getNbPoints(FILE_NAME);
        
        MapPoints mp = new MapPoints(nbPoints);
        
        mp.initialize();
        
    }
    
     public void getNbPoints(String fileName) {
        
        try {
            FileInputStream fis = new FileInputStream(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            
            String line;
            
            while((line = br.readLine()) != null){
                nbPoints++;
            }
            
            br.close();
            isr.close();
            fis.close();
        }
        catch (IOException | NumberFormatException e) {
            System.out.println(e.toString());
        }
    }
    
    public static void shutdown ( ) {
    
    }
    
    public static void processEvent(Event event) {
        
    }
}
