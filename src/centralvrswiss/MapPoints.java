package centralvrswiss;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nathan
 */
public class MapPoints {
    
    private final char TAB = '\t';
    
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
            int count = 1;
            
            // Reading line by line the .dat file
            while((line = br.readLine()) != null){
                splitter = line.split("  "); // Separating in an Array when finding 
                                             // 2 spaces
                currentX = Double.parseDouble(splitter[count]);
                count++;
                
                // Detecting the -9999.99 values
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
                xAxe.add(currentX);
                yAxe.add(currentY);
                zAxe.add(currentZ);
                
                count = 1;
            }
            System.out.println("Bruh"); // End of the lecture, erase when we will end
            
            br.close();
            ipsr.close();
            ips.close();
            
        } catch (IOException ex) {
            Logger.getLogger(CentralVRSwiss.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void JAVA_to_VRML() {
        try {
            PrintWriter pw = new PrintWriter(new File("Test.wrl"));
            StringBuilder sb = new StringBuilder();
            
            // Creating the .wrl file for VR
            sb.append("#VRML V2.0 utf8\n");
            sb.append("# Test1\n");
            sb.append("Shape {\n");
            sb.append(TAB).append("geometry PointSet {\n");
            
            // Inserting points in the file
            sb.append(TAB).append(TAB).append("coord Coordinate {\n");
            sb.append(TAB).append(TAB).append(TAB).append("point [\n");
            for(int i = 0; i < xAxe.size(); ++i) {
                sb.append(TAB).append(TAB).append(TAB).append(TAB).
                    append(xAxe.get(i)).append(' ').
                    append(yAxe.get(i)).append(' ').
                    append(zAxe.get(i)).append('\n');
            }
            sb.append(TAB).append(TAB).append(TAB).append("]\n");
            sb.append(TAB).append(TAB).append("}\n");
            sb.append(TAB).append("}\n");
            
            // Giving a color to the block
            sb.append(TAB).append("appearance Appearance {\n");
            sb.append(TAB).append(TAB).append("material Material {\n");
            sb.append(TAB).append(TAB).append(TAB).append("diffuseColor ").append("1 0 0 #simple red\n");
            sb.append(TAB).append(TAB).append("}\n");
            sb.append(TAB).append("}\n");
            sb.append("}\n");

//    Shape {
//	geometry PointSet {
//		coord Coordinate {
//                    point [
//                        -1.0 -1.0 0.0,
//                        1.0 1.0 0.0,
//                        0.0 0.0 0.0,
//                    ]
//                }
//                color Color {
//                    color [
//                        1.0 0.0 0.0,
//                        0.0 1.0 0.0,
//                        0.0 0.0 1.0,
//                    ]
//                }
//            }
//        }
            
            pw.write(sb.toString());
            pw.close();
            
        } catch(FileNotFoundException ex){
            Logger.getLogger(MapPoints.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("入出力エラー");
        }
    }
}

