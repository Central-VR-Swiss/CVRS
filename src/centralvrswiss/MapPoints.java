package centralvrswiss;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.ProcessBuilder;
import java.lang.Process;

/**
 *
 * @author Nathan
 */
public class MapPoints {
    
    private final char TAB = '\t';
    private final String RED = "1.0 0.0 0.0";
    private final String GREEN = "0.0 1.0 0.0";
    private final String BLUE = "0.0 0.0 1.0";
    private final int xs = 1132;
    // xs is the number obtained by finding the minimum value and the maximum value of x and adding 1 to the difference
    private final int ys = 925;
    // ys is same as xs.
    private final double dif = 10;
    // dif can change the threshold about detecting difference.
    private final double er = -9999.99;
    private final double s_dif = 1.5;
    //s_dif can change the threshold about deleting points
    
    private String color;
    
    private double[][] zAxe = new double[xs][ys];
    private int[][] idx = new int[xs][ys];


    
    private double cX;
    private double cY;
    private double cZ;

    private int[] triangle1;
    private int[] triangle2;
    private int[] triangle3;
 
    public MapPoints(int nbPoints){

    }
    
    public void initialize() {
        try {
            FileInputStream ips = new FileInputStream("points/53394620_dsm_1m.dat");
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            
            String line;
            String[] splitter;
            int x;
            int y;

            for(int i=0; i<xs; ++i){
                for(int j=0; j<ys; ++j){
                    zAxe[i][j] = 0;
                    idx[i][j] = 0;
                }
            }
            
            int ix = 1;
            // Reading line by line the .dat file
            while((line = br.readLine()) != null){
                splitter = line.split("[\\s]+"); // Separating in an Array
                cX = Double.parseDouble(splitter[1]);
                cY = Double.parseDouble(splitter[2]);
                cZ = Double.parseDouble(splitter[3]);
                x = (int)(cX + 7541);
                y = (int)(cY + 35126);
                // Puting each value in the array
                zAxe[x][y] = cZ;
                idx[x][y] = ix;
                ix++;
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
            sb.append(TAB).append("geometry IndexedFaceSet {\n");
            
            // Inserting points in the file
            sb.append(TAB).append(TAB).append("coord Coordinate {\n");
            sb.append(TAB).append(TAB).append(TAB).append("point [\n");
            for(int i = 0; i < xs; ++i) {
                for(int j = 0; j < ys; ++j){
                    if(zAxe[i][j]==er){
                        zAxe[i][j] = deleteErrorPoints(i,j);
                    }
                    reducePoints(i,j);
                    if(idx[i][j] > 0){
                        
                        sb.append(TAB).append(TAB).append(TAB).append(TAB).
                            append(i).append(' ').
                            append(j).append(' ').
                            append(zAxe[i][j]).append('\n');
                    }
                }
            }
            sb.append(TAB).append(TAB).append(TAB).append("]\n");
            sb.append(TAB).append(TAB).append("}\n");
            

            writePoints();
            callPython();
            readTriangle();

            sb.append(TAB).append(TAB).append("coordIndex [\n");
            for(int i=0;i<triangle1.length;i++){
                sb.append(TAB).append(TAB).append(TAB).
                            append(triangle1[i]).append(' ').
                            append(triangle2[i]).append(' ').
                            append(triangle3[i]).append(' ').
                            append(-1).append('\n');
            }
            sb.append(TAB).append(TAB).append("]\n");


            /*
            sb.append(TAB).append(TAB).append("color Color {\n");
            sb.append(TAB).append(TAB).append(TAB).append("color [\n");
            for(int i = 0; i < xs; ++i) {
                for(int j = 0; j < ys; ++j){
                    if(idx[i][j] > 0){
                        if(zAxe[i][j] < 0){
                            color = BLUE;
                        }else if(check(i,j)){
                            color = RED;
                        }else{
                            color = GREEN;
                        }
                    
                
                        sb.append(TAB).append(TAB).append(TAB).append(TAB).
                            append(color).append('\n');
                    }
                }
            }
            sb.append(TAB).append(TAB).append(TAB).append("]\n");
            sb.append(TAB).append(TAB).append("}\n");
            */
            sb.append(TAB).append("}\n");
            
            sb.append("}\n");
            
            // Giving a color to the block
//            sb.append(TAB).append("appearance Appearance {\n");
//            sb.append(TAB).append(TAB).append("material Material {\n");
//            sb.append(TAB).append(TAB).append(TAB).append("diffuseColor ").append("1 0 0 #simple red\n");
//            sb.append(TAB).append(TAB).append("}\n");
//            sb.append(TAB).append("}\n");
//            sb.append("}\n");

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
    
    public boolean check(int i, int j){
        double tmp = 0.0;
        //There are 9 types of filter.
        if((i>1) && (j>1) && (i<xs-1) && (j<ys-1)){
            for(int a=-1; a<2; ++a){
                for(int b=-1; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, dif)){
                        return true;
                    }
                }
            }
        }else if((j>1) && (i<xs-1) && (j<ys-1)){
            for(int a=0; a<2; ++a){
                for(int b=-1; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, dif)){
                        return true;
                    }
                }
            }
        }else if((i>1) && (i<xs-1) && (j<ys-1)){
            for(int a=-1; a<2; ++a){
                for(int b=0; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, dif)){
                        return true;
                    }
                }
            }
        }else if((i>1) && (j>1) && (i<xs-1)){
            for(int a=-1; a<2; ++a){
                for(int b=-1; b<1; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, dif)){
                        return true;
                    }
                }
            }
        }else if((i>1) && (j>1) && (j<ys-1)){
            for(int a=-1; a<1; ++a){
                for(int b=-1; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, dif)){
                        return true;
                    }
                }
            }
        }else if((i>1) && (i<xs-1)){
            for(int a=-1; a<2; ++a){
                for(int b=0; b<1; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, dif)){
                        return true;
                    }
                }
            }
        }else if((i>1) && (j<ys-1)){
            for(int a=-1; a<1; ++a){
                for(int b=0; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, dif)){
                        return true;
                    }
                }
            }
        }else if((j>1) && (i<xs-1)){
            for(int a=0; a<2; ++a){
                for(int b=-1; b<1; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, dif)){
                        return true;
                    }
                }
            }
        }else if((j>1) && (j<ys-1)){
            for(int a=0; a<1; ++a){
                for(int b=-1; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, dif)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public double deleteErrorPoints(int i,int j){
        int cnt = 0;
        double tmp = 0;
        if((i>1) && (j>1) && (i<xs-1) && (j<ys-1)){
            for(int a=-1; a<2; ++a){
                for(int b=-1; b<2; ++b){
                    deleteError(zAxe, i, j, a, b, tmp, cnt);
                }
            }
        }else if((j>1) && (i<xs-1) && (j<ys-1)){
            for(int a=0; a<2; ++a){
                for(int b=-1; b<2; ++b){
                    deleteError(zAxe, i, j, a, b, tmp, cnt);
                }
            }
        }else if((i>1) && (i<xs-1) && (j<ys-1)){
            for(int a=-1; a<2; ++a){
                for(int b=0; b<2; ++b){
                    deleteError(zAxe, i, j, a, b, tmp, cnt);
                }
            }
        }else if((i>1) && (j>1) && (i<xs-1)){
            for(int a=-1; a<2; ++a){
                for(int b=-1; b<1; ++b){
                    deleteError(zAxe, i, j, a, b, tmp, cnt);
                }
            }
        }else if((i>1) && (j>1) && (j<ys-1)){
            for(int a=-1; a<1; ++a){
                for(int b=-1; b<2; ++b){
                    deleteError(zAxe, i, j, a, b, tmp, cnt);
                }
            }
        }else if((i>1) && (i<xs-1)){
            for(int a=-1; a<2; ++a){
                for(int b=0; b<1; ++b){
                    deleteError(zAxe, i, j, a, b, tmp, cnt);
                }
            }
        }else if((i>1) && (j<ys-1)){
            for(int a=-1; a<1; ++a){
                for(int b=0; b<2; ++b){
                    deleteError(zAxe, i, j, a, b, tmp, cnt);
                }
            }
        }else if((j>1) && (i<xs-1)){
            for(int a=0; a<2; ++a){
                for(int b=-1; b<1; ++b){
                    deleteError(zAxe, i, j, a, b, tmp, cnt);
                }
            }
        }else if((j>1) && (j<ys-1)){
            for(int a=0; a<1; ++a){
                for(int b=-1; b<2; ++b){
                    deleteError(zAxe, i, j, a, b, tmp, cnt);
                }
            }
        }
        return tmp/cnt;
    }
    
    public void reducePoints(int i,int j){
        double tmp = 0.0;
        boolean del = true;
        
        if((i>1) && (j>1) && (i<xs-1) && (j<ys-1)){
            for(int a=-1; a<2; ++a){
                for(int b=-1; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, s_dif)){
                        del = false;
                    }
                }
            }
        }else if((j>1) && (i<xs-1) && (j<ys-1)){
            for(int a=0; a<2; ++a){
                for(int b=-1; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, s_dif)){
                        del = false;
                    }
                }
            }
        }else if((i>1) && (i<xs-1) && (j<ys-1)){
            for(int a=-1; a<2; ++a){
                for(int b=0; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, s_dif)){
                        del = false;
                    }
                }
            }
        }else if((i>1) && (j>1) && (i<xs-1)){
            for(int a=-1; a<2; ++a){
                for(int b=-1; b<1; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, s_dif)){
                        del = false;
                    }
                }
            }
        }else if((i>1) && (j>1) && (j<ys-1)){
            for(int a=-1; a<1; ++a){
                for(int b=-1; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, s_dif)){
                        del = false;
                    }
                }
            }
        }else if((i>1) && (i<xs-1)){
            for(int a=-1; a<2; ++a){
                for(int b=0; b<1; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, s_dif)){
                        del = false;
                    }
                }
            }
        }else if((i>1) && (j<ys-1)){
            for(int a=-1; a<1; ++a){
                for(int b=0; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, s_dif)){
                        del = false;
                    }
                }
            }
        }else if((j>1) && (i<xs-1)){
            for(int a=0; a<2; ++a){
                for(int b=-1; b<1; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, s_dif)){
                        del = false;
                    }
                }
            }
        }else if((j>1) && (j<ys-1)){
            for(int a=0; a<1; ++a){
                for(int b=-1; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, s_dif)){
                        del = false;
                    }
                }
            }
        }
        if(del) idx[i][j] = 0;
    }
    
    public boolean checkReturn(double tabZ[][], int i, int j, int a, int b, double tmp, double dif) {
        tmp = Math.abs(tabZ[i][j] - tabZ[i+a][j+b]);
        return tmp > dif;
    }
    
    public void deleteError(double tabZ[][], int i, int j, int a, int b, double tmp, double cnt) {
        if(zAxe[i][j]!=er){
            tmp += zAxe[i+a][j+b];
            cnt++;
        }
    }
    
    public void callPython(){
        try{
            ProcessBuilder pb = new ProcessBuilder("Delaunay.bat");
            Process process = pb.start();
            int ret = process.waitFor();
            System.out.println("finish!")
        }catch(IOException ex){
            System.out.println(ex);
        }catch(InterruptedException iex){
            System.out.println(iex);
        }
    }
    
    public void writePoints(){
        try{
            PrintWriter p = new PrintWriter(new File("Delaunay.dat"));
            StringBuilder s = new StringBuilder();

            for(int i=0;i<xs;i++){
                for(int j=0;j<ys;j++){
                    if(idx[i][j]>0){
                        s.append(i).append(" ").append(j).append(" ").append(zAxe[i][j]).append("\n");
                    }
                }
            }
            p.write(s.toString());
            p.close();
        }catch(IOException e){
            System.out.println(e);
        }
    }

    public void readTriangle(){
        try{
            FileInputStream fis = new FileInputStream("Delaunay.dat");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String tri;
            String[] splitter2;
            int a1;
            int a2;
            int a3;
            int cnt = 0;


            while((tri = br.readLine()) != null){
                cnt++;
            }
            FileInputStream f = new FileInputStream("Delaunay.dat");
            InputStreamReader is = new InputStreamReader(f);
            BufferedReader b = new BufferedReader(is);

            triangle1 = new int[cnt];
            triangle2 = new int[cnt];
            triangle3 = new int[cnt];
            
            // Reading line by line the Delaunay.dat file
            cnt = 0;
            while((tri = b.readLine()) != null){
                splitter2 = tri.split("[\\s]+"); // Separating in an Array
                a1 = Integer.parseInt(splitter2[0]);
                a2 = Integer.parseInt(splitter2[1]);
                a3 = Integer.parseInt(splitter2[2]);
                triangle1[cnt] = a1;
                triangle2[cnt] = a2;
                triangle3[cnt] = a3;
            }
            System.out.println("done!");
        }catch(IOException e){
            System.out.println(e);
        }
    }

}
