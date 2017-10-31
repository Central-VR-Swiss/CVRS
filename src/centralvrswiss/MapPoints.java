/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralvrswiss;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nathan
 */
public class MapPoints {
    
    private ArrayList xAxe;
    private ArrayList yAxe;
    private ArrayList zAxe;
    
    private double currentX;
    private double currentY;
    private double currentZ;

    public MapPoints(int nbPoints) {
        xAxe = new ArrayList(nbPoints);
        yAxe = new ArrayList(nbPoints);
        zAxe = new ArrayList(nbPoints);
    }    
    
    public void initialize() {
        try {
            FileInputStream ips = new FileInputStream("points/53394620_dsm_1m.dat");
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            
            String line;
            String[] splitter;
            int count = 1, a = 0;
            
            while((line = br.readLine()) != null){
                splitter = line.split("  ");
                currentX = Double.parseDouble(splitter[count]);
                count++;
                
                if(splitter[count].contains("-9999.99")){
                    String temp = splitter[count];
                    String[] splitter2 = temp.split(" ");
                    currentY = Double.parseDouble(splitter2[0]);
                    splitter[count] = splitter2[1]; // "-9999.99"
                }else{
                    currentY = Double.parseDouble(splitter[count]);
                    count++;
                }
                
                if(splitter[count].isEmpty()){
                    count++;
                }
                
                if(splitter[count].charAt(0) == ' '){
                    splitter[count] = splitter[count].replaceAll(" ", "");
                }
                currentZ = Double.parseDouble(splitter[count]);
                // Puting each value in the array
//                System.out.println(++a + " Wa " + currentX + " We " + currentY + " Wi " + currentZ);
                xAxe.add(currentX);
                yAxe.add(currentY);
                zAxe.add(currentZ);
                
                count = 1;
            }
            System.out.println("Bruh");
            
            br.close();
            ipsr.close();
            ips.close();
            
        } catch (IOException ex) {
            Logger.getLogger(CentralVRSwiss.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
