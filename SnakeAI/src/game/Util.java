package game;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class Util {
    public static String getSystemTime() {
        String time = "";
        Calendar calendar = Calendar.getInstance();
        int YY = calendar.get(Calendar.YEAR) ;
        int MM = calendar.get(Calendar.MONTH)+1;
        int DD = calendar.get(Calendar.DATE);
        int HH = calendar.get(Calendar.HOUR_OF_DAY);
        int mm = calendar.get(Calendar.MINUTE);
        int SS = calendar.get(Calendar.SECOND);
        int MI = calendar.get(Calendar.MILLISECOND);
        time += YY + "/" + MM + "/" + DD + "-" + HH + ":" + mm + ":" + SS + ":" + MI;
        return time;
    }

    public static void write2Text(String str, String filepath) {
        FileWriter fw;
        File f = new File(filepath);
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            fw = new FileWriter(f);
            BufferedWriter out = new BufferedWriter(fw);
            out.write(str);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printMap(int[][] map,String filepath) {
        String temp = "";
        temp += "\t";
        for(int i=0; i<map[0].length; i++)
            temp += i + "\t";
        temp += "\n";

        for(int i=0; i<map.length; i++) {
            temp += i + "\t";
            for(int j=0; j<map[0].length; j++)
                temp += map[i][j] + "\t";
            temp += "\n";
        }
        try {
            write2Text(temp,filepath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getPixel (int i, int padding, int pixelPerUnit) {
        return 1 + padding + i * pixelPerUnit;
    }

    public static int dim2ToDim1(Position coordinate, int colPerRow) {
        int x = coordinate.getX();
        int y = coordinate.getY();
        return x * colPerRow + y;
    }
}
