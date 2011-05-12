package net.sourceforge.mipa.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class combineInterval {
   
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        readNP("NormalProcess0");
        readNP("NormalProcess1");
    }
    
    private static void readNP(String file) throws IOException {
        String path = "log/"+file+"_combined.log";
        file = "log/"+file+".log";
        String intervalLeft = null;
        String intervalLeftLo = null;
        String intervalLeftHi = null;
        String intervalRight = null;
        String intervalRightLo = null;
        String intervalRightHi = null;
        BufferedReader r = new BufferedReader(new FileReader(file));
        BufferedWriter w = new BufferedWriter(new FileWriter(path));
        String s = r.readLine();
        while(s!=null) {
            StringTokenizer st = new StringTokenizer(s);
            if(st.hasMoreTokens()) {
                intervalLeft = st.nextToken();
                intervalLeftLo = st.nextToken();
                intervalLeftHi = st.nextToken();
            }
            if(intervalLeftHi.equals("null")) {
                s = r.readLine();
                st = new StringTokenizer(s);
                intervalRight = st.nextToken();
                intervalRightLo = st.nextToken();
                intervalRightHi = st.nextToken();
                intervalLeftHi = intervalRightHi;
                w.write(intervalLeft+" "+intervalLeftLo+" "+intervalLeftHi+"\n");
                w.flush();
            }
            else {
                w.write(s+"\n");
                w.flush();
            }
            s = r.readLine();
        }
    }
}
