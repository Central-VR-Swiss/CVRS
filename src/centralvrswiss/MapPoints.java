package centralvrswiss;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nathan
 */
public class MapPoints {
    
    private final char TAB = '\t';
    private final String RED = "1.0 0.0 0.0";
    private final String GREEN = "0.0 1.0 0.0";
    private final String BLUE = "0.0 0.0 1.0";
    private final String FIRST_FILE = "53394620_dsm_1m.dat";
    private final String DELAUNAY_FILE = "Delaunay.dat";
    
    private final int XS = 1132;
    // XS is the number obtained by finding the minimum value and the maximum value of x and adding 1 to the difference
    private final int YS = 925;
    // YS is same as XS.
    private final double DIF = 10;
    // dif can change the threshold about detecting difference.
    private final double ERR = -9999.99;
    private final double S_DIF = 1.5;
    //s_dif can change the threshold about deleting points
    
    // private String color;
    
    private double[][] zAxe = new double[XS][YS];
    private int[][] idx = new int[XS][YS];
    
    private double cX;
    private double cY;
    private double cZ;

    private int[] triangle1;
    private int[] triangle2;
    private int[] triangle3;

    private int[] cidx;
    private int cid_c;
    private int kc = 0;
 
    public MapPoints(int nbPoints){
        cidx = new int[nbPoints];
    }
    
    public void initialize() {
        try {
            FileInputStream ips = new FileInputStream(FIRST_FILE);
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            
            String line;
            String[] splitter;
            int x;
            int y;

            for(int i=0; i<XS; ++i){
                for(int j=0; j<YS; ++j){
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
            boolean c;
            int t;
            int xc1 = 0;
            int xc2 = 0;

            
            // Creating the .wrl file for VR
            sb.append("#VRML V2.0 utf8\n");
            sb.append("# Test1\n");
            sb.append("Shape {\n");
            sb.append(TAB).append("geometry IndexedFaceSet {\n");
            
            // Inserting points in the file
            sb.append(TAB).append(TAB).append("coord Coordinate {\n");
            sb.append(TAB).append(TAB).append(TAB).append("point [\n");
            for(int i = 0; i < XS; ++i) {
                for(int j = 0; j < YS; ++j){
                    if(zAxe[i][j]==ERR){
                        zAxe[i][j] = deleteErrorPoints(i,j);
                    }
                    reducePoints(i,j);
                    zAxe[0][0] = 0; // 0
                    zAxe[0][YS-1] = 0;
                    zAxe[XS-1][0] = 0;
                    zAxe[XS-1][YS-1] = 0;
                    idx[0][0] = 1;
                    idx[0][YS-1] = 1;
                    idx[XS-1][0] = 1;
                    idx[XS-1][YS-1] = 1;
                    if(idx[i][j] > 0){
                        if(i==0) xc2++;
                        sb.append(TAB).append(TAB).append(TAB).append(TAB).
                            append(i).append(' ').
                            append(j).append(' ').
                            append(zAxe[i][j]).append('\n');
                    }
                }
            }

            sb.append(TAB).append(TAB).append(TAB).append(TAB).append(0).append(' ').append(0).append(' ').append(-50).append('\n');
            sb.append(TAB).append(TAB).append(TAB).append(TAB).append(0).append(' ').append(YS-1).append(' ').append(-50).append('\n');
            sb.append(TAB).append(TAB).append(TAB).append(TAB).append(XS-1).append(' ').append(0).append(' ').append(-50).append('\n');
            sb.append(TAB).append(TAB).append(TAB).append(TAB).append(XS-1).append(' ').append(YS-1).append(' ').append(-50).append('\n');
            
            sb.append(TAB).append(TAB).append(TAB).append("]\n");
            sb.append(TAB).append(TAB).append("}\n");
            
            writePoints();
            callPython();
            readTriangle();

            int ln = triangle1.length;
            System.out.println(ln);

            sb.append(TAB).append(TAB).append("coordIndex [\n");
            
            for(int i=0;i<triangle1.length;i++){
                sb.append(TAB).append(TAB).append(TAB).
                            append(triangle1[i]).append(", ").
                            append(triangle2[i]).append(", ").
                            append(triangle3[i]).append(", ").
                            append("-1,").append('\n');
            }
            
            
            sb.append(TAB).append(TAB).append(TAB).append(kc).append(", ").append(kc+1).append(", ").append(kc+3).append(", ").append(kc+2).append(",-1,\n");

            sb.append(TAB).append(TAB).append(TAB).append(0).append(", ").append(kc).append(", ").append(kc+2).append(", ").append(kc-2).append(",-1,\n");//front
            sb.append(TAB).append(TAB).append(TAB).append(kc-2).append(", ").append(kc+2).append(", ").append(kc+3).append(", ").append(kc-1).append(",-1,\n");//right side
            sb.append(TAB).append(TAB).append(TAB).append(1).append(", ").append(kc-1).append(", ").append(kc+3).append(", ").append(kc+1).append(",-1,\n");//left side
            sb.append(TAB).append(TAB).append(TAB).append(0).append(", ").append(1).append(", ").append(kc+1).append(", ").append(kc).append(",-1\n");
            
            
            
            
            
            sb.append(TAB).append(TAB).append("]\n");

            sb.append(TAB).append(TAB).append("colorPerVertex FALSE\n");
            sb.append(TAB).append(TAB).append("solid FALSE\n");

            // sb.append(TAB).append(TAB).append("color Color {\n");
            // sb.append(TAB).append(TAB).append(TAB).append("color [\n");

            // sb.append(TAB).append(TAB).append(TAB).append(TAB).append(BLUE);
            // sb.append(TAB).append(TAB).append(TAB).append(TAB).append(GREEN);

            // sb.append(TAB).append(TAB).append(TAB).append("]\n");
            // sb.append(TAB).append(TAB).append("}\n");

            // sb.append(TAB).append(TAB).append("colorIndex[\n");
            // for(int i=0;i<triangle1.length;i++){
            //     c = true;
            //     for(int j=0;j<cid_c;j++){
            //         t = cidx[j];
            //         if(t==triangle1[i] || t==triangle2[i] || t==triangle3[i]){
            //             sb.append(TAB).append(TAB).append(TAB).append(1).append(" \n");
            //             c = false;
            //             break;
            //         }
            //     }
            //     if(c){
            //         sb.append(TAB).append(TAB).append(TAB).append(0).append(" \n");
            //     }
            // }
            // sb.append(TAB).append(TAB).append("]\n");
            sb.append(TAB).append("}\n");



            sb.append(TAB).append("appearance Appearance{\n");
            sb.append(TAB).append(TAB).append("material Material{\n");
            sb.append(TAB).append(TAB).append(TAB).append("diffuseColor 1 0 0\n");
            sb.append(TAB).append(TAB).append("}\n");
            sb.append(TAB).append("}\n");

            sb.append("}\n");
            
            System.out.println("Yeeey");
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
        if((i>1) && (j>1) && (i<XS-1) && (j<YS-1)){
            for(int a=-1; a<2; ++a){
                for(int b=-1; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, DIF)){
                        return true;
                    }
                }
            }
        }else if((j>1) && (i<XS-1) && (j<YS-1)){
            for(int a=0; a<2; ++a){
                for(int b=-1; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, DIF)){
                        return true;
                    }
                }
            }
        }else if((i>1) && (i<XS-1) && (j<YS-1)){
            for(int a=-1; a<2; ++a){
                for(int b=0; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, DIF)){
                        return true;
                    }
                }
            }
        }else if((i>1) && (j>1) && (i<XS-1)){
            for(int a=-1; a<2; ++a){
                for(int b=-1; b<1; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, DIF)){
                        return true;
                    }
                }
            }
        }else if((i>1) && (j>1) && (j<YS-1)){
            for(int a=-1; a<1; ++a){
                for(int b=-1; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, DIF)){
                        return true;
                    }
                }
            }
        }else if((i>1) && (i<XS-1)){
            for(int a=-1; a<2; ++a){
                for(int b=0; b<1; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, DIF)){
                        return true;
                    }
                }
            }
        }else if((i>1) && (j<YS-1)){
            for(int a=-1; a<1; ++a){
                for(int b=0; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, DIF)){
                        return true;
                    }
                }
            }
        }else if((j>1) && (i<XS-1)){
            for(int a=0; a<2; ++a){
                for(int b=-1; b<1; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, DIF)){
                        return true;
                    }
                }
            }
        }else if((j>1) && (j<YS-1)){
            for(int a=0; a<1; ++a){
                for(int b=-1; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, DIF)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public double deleteErrorPoints(int i,int j){
        int cnt = 0;
        double tmp = 0.0;
        if((i>1) && (j>1) && (i<XS-1) && (j<YS-1)){
            for(int a=-1; a<2; ++a){
                for(int b=-1; b<2; ++b){
                    if(zAxe[i+a][j+b]!=ERR){
                        tmp += zAxe[i+a][j+b];
                        cnt++;
                    }
                }
            }
        }else if((j>1) && (i<XS-1) && (j<YS-1)){
            for(int a=0; a<2; ++a){
                for(int b=-1; b<2; ++b){
                    if(zAxe[i+a][j+b]!=ERR){
                        tmp += zAxe[i+a][j+b];
                        cnt++;
                    }
                }
            }
        }else if((i>1) && (i<XS-1) && (j<YS-1)){
            for(int a=-1; a<2; ++a){
                for(int b=0; b<2; ++b){
                    if(zAxe[i+a][j+b]!=ERR){
                        tmp += zAxe[i+a][j+b];
                        cnt++;
                    }
                }
            }
        }else if((i>1) && (j>1) && (i<XS-1)){
            for(int a=-1; a<2; ++a){
                for(int b=-1; b<1; ++b){
                    if(zAxe[i+a][j+b]!=ERR){
                        tmp += zAxe[i+a][j+b];
                        cnt++;
                    }
                }
            }
        }else if((i>1) && (j>1) && (j<YS-1)){
            for(int a=-1; a<1; ++a){
                for(int b=-1; b<2; ++b){
                    if(zAxe[i+a][j+b]!=ERR){
                        tmp += zAxe[i+a][j+b];
                        cnt++;
                    }
                }
            }
        }else if((i>1) && (i<XS-1)){
            for(int a=-1; a<2; ++a){
                for(int b=0; b<1; ++b){
                    if(zAxe[i+a][j+b]!=ERR){
                        tmp += zAxe[i+a][j+b];
                        cnt++;
                    }
                }
            }
        }else if((i>1) && (j<YS-1)){
            for(int a=-1; a<1; ++a){
                for(int b=0; b<2; ++b){
                    if(zAxe[i+a][j+b]!=ERR){
                        tmp += zAxe[i+a][j+b];
                        cnt++;
                    }
                }
            }
        }else if((j>1) && (i<XS-1)){
            for(int a=0; a<2; ++a){
                for(int b=-1; b<1; ++b){
                    if(zAxe[i+a][j+b]!=ERR){
                        tmp += zAxe[i+a][j+b];
                        cnt++;
                    }
                }
            }
        }else if((j>1) && (j<YS-1)){
            for(int a=0; a<1; ++a){
                for(int b=-1; b<2; ++b){
                    if(zAxe[i+a][j+b]!=ERR){
                        tmp += zAxe[i+a][j+b];
                        cnt++;
                    }
                }
            }
        }
        return tmp/cnt;
    }
    
    public void reducePoints(int i,int j){
        boolean line = false;
        double tmp = 0.0;
        int dcnt = 0;
        int[][] did = new int[3][3];

        for(int k=0;k<3;k++){
            for(int l=0;l<3;l++){
                did[k][l] = 0;
            }
        }

        did[1][1] = 1;
        
        if((i>1) && (j>1) && (i<XS-1) && (j<YS-1)){
            for(int a=-1; a<2; ++a){
                for(int b=-1; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, S_DIF)){
                        did[a+1][b+1] = 1;
                    }
                }
            }
        }else if((j>1) && (i<XS-1) && (j<YS-1)){
            for(int a=0; a<2; ++a){
                for(int b=-1; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, S_DIF)){
                        did[a+1][b+1] = 1;
                    }
                }
            }
        }else if((i>1) && (i<XS-1) && (j<YS-1)){
            for(int a=-1; a<2; ++a){
                for(int b=0; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, S_DIF)){
                        did[a+1][b+1] = 1;
                    }
                }
            }
        }else if((i>1) && (j>1) && (i<XS-1)){
            for(int a=-1; a<2; ++a){
                for(int b=-1; b<1; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, S_DIF)){
                        did[a+1][b+1] = 1;            
                    }
                }
            }
        }else if((i>1) && (j>1) && (j<YS-1)){
            for(int a=-1; a<1; ++a){
                for(int b=-1; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, S_DIF)){
                        did[a+1][b+1] = 1;
                    }
                }
            }
        }else if((i>1) && (i<XS-1)){
            for(int a=-1; a<2; ++a){
                for(int b=0; b<1; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, S_DIF)){
                        did[a+1][b+1] = 1;
                    }
                }
            }
        }else if((i>1) && (j<YS-1)){
            for(int a=-1; a<1; ++a){
                for(int b=0; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, S_DIF)){
                        did[a+1][b+1] = 1;
                    }
                }
            }
        }else if((j>1) && (i<XS-1)){
            for(int a=0; a<2; ++a){
                for(int b=-1; b<1; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, S_DIF)){
                        did[a+1][b+1] = 1;
                    }
                }
            }
        }else if((j>1) && (j<YS-1)){
            for(int a=0; a<1; ++a){
                for(int b=-1; b<2; ++b){
                    if(checkReturn(zAxe, i, j, a, b, tmp, S_DIF)){
                        did[a+1][b+1] = 1;
                    }
                }
            }
        }
        for(int r=0;r<3;r++){
            for(int l=0;l<3;l++){
                if(did[r][l]==1){
                    dcnt++;
                }
            }
        }
        if(((did[0][0]==1) && (did[2][2]==1)) || 
            ((did[0][2]==1) && (did[2][0]==1)) || 
            ((did[0][1]==1) && (did[2][1]==1)) || 
            ((did[1][0]==1) && (did[1][2]==1))){
                line = true;
            }
        if(dcnt==9 || line) idx[i][j] = 0;
        for(int r=0;r<XS;r++){
            idx[r][0] = 0;
            idx[r][YS-1] = 0;
        }
        for(int l=0;l<YS;l++){
            idx[XS-1][l] = 0;
            idx[0][l] = 0;
        }
    }
    
    public boolean checkReturn(double tabZ[][], int i, int j, int a, int b, double tmp, double dif) {
        tmp = Math.abs(tabZ[i][j] - tabZ[i+a][j+b]);
        return tmp > dif;
    }
    
    
    public void callPython(){
        try{
            ProcessBuilder pb = new ProcessBuilder("Delaunay.bat");
            Process process = pb.start();
            int ret = process.waitFor();
            System.out.println("finish!");
        }catch(IOException | InterruptedException ex){
            System.out.println("Shit");
            System.out.println(ex);
        }
    }
    
    public void writePoints(){
        try{
//            File file = new File("Delaunay.dat");
//            System.out.println(file.getCanonicalPath());
//            if (checkBeforeWritefile(file)){
//                System.out.println("Yolooo");
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(DELAUNAY_FILE)));
                int ic = 0;
                int k = 0;
                for(int i=0;i<XS;i++){
                    for(int j=0;j<YS;j++){
                        if(idx[i][j]>0){
                            kc++;
                            if(zAxe[i][j]>18){
                                cidx[k] = ic;
                                k++;
                            }
                            ic++;
                        }
                    }
                }
                System.out.println(kc);
                cid_c = k;
                pw.close();
//            }else{
//                System.out.println("ファイルに書き込めません");
//            }
            PrintWriter p = new PrintWriter(new File("Delaunay.dat"));
            StringBuilder s = new StringBuilder();

            for(int i=0;i<XS;i++){
                for(int j=0;j<YS;j++){
                    if(idx[i][j]>0){
                        s.append(i).append(" ").append(j).append(" ").append(zAxe[i][j]).append("\n");
                    }
                }
            }
            p.write(s.toString());
            p.close();
            System.out.println("written!");
            System.out.println(k);
        }catch(IOException e){
            System.out.println(e);
        }
    }

//    private static boolean checkBeforeWritefile(File file){
//        System.out.println(file.getName());
//        if (file.exists()){
//            System.out.println("Yeeey");
//            if (file.isFile() && file.canWrite()){
//                return true;
//            }
//        }
//
//        return false;
//    }
    
//    public void readTriangle() {
//        try {
//            FileInputStream fis = new FileInputStream(DELAUNAY_FILE);
//            InputStreamReader isr = new InputStreamReader(fis);
//            BufferedReader br = new BufferedReader(isr);
//            
//            String triLine;
//            
//            while((triLine = br.readLine()) != null) {
//                triangle = triLine.split(",");
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(CentralVRSwiss.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    public void readTriangle(){
        try{
            FileInputStream fis = new FileInputStream(DELAUNAY_FILE);
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
            
            FileInputStream f = new FileInputStream(DELAUNAY_FILE);
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
                cnt++;
            }

            System.out.println("done!");
            
            b.close();
            is.close();
            f.close();
            
            br.close();
            isr.close();
            fis.close();
            
        }catch(IOException e){
            System.out.println(e);
        }
    }
}


//            try {
//                FileInputStream fis = new FileInputStream(DELAUNAY_FILE);
//                InputStreamReader isr = new InputStreamReader(fis);
//                BufferedReader br = new BufferedReader(isr);
//
//                String triLine;
//                String[] triangle;
////                String[] points = new String[XS*YS];
//                int k = 0;
//                
//                while((triLine = br.readLine()) != null) {
//                    triangle = triLine.split(",");
//                    sb.append(TAB).append(TAB).append(TAB);
//                    for(int i = 0; i < triangle.length - 1; ++i){
//                        sb.append(triangle[i]).append(", ");
////                        points[k++] = triangle[i];
//                    }
//                    sb.append("-1,").append('\n');
//                }
//                sb.setCharAt(sb.length() - 2, ' ');", "
//                
//                br.close();
//                isr.close();
//                fis.close();
//            } catch (IOException ex) {
//                Logger.getLogger(CentralVRSwiss.class.getName()).log(Level.SEVERE, null, ex);
//            }

            /*
            sb.append(TAB).append(TAB).append("color Color {\n");
            sb.append(TAB).append(TAB).append(TAB).append("color [\n");
            for(int i = 0; i < XS; ++i) {
                for(int j = 0; j < YS; ++j){
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
            
            // Giving a color to the block
//            sb.append(TAB).append("appearance Appearance {\n");
//            sb.append(TAB).append(TAB).append("material Material {\n");
//            sb.append(TAB).append(TAB).append(TAB).append("diffuseColor ").append("1 0 0 #simple red\n");
//            sb.append(TAB).append(TAB).append("}\n");
//            sb.append(TAB).append("}\n");
//            sb.append("}\n");
