package config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Config {

    public static String KeyIp = "";

    public static String Secret = "";

    public static String IpUrl = "";

    public static int Time = 60 * 60 * 1000;

    public static String[] Domain = null;

    public static String[] SubDomain = null;

    public static String Server = "";

    public static String Config = "";

    public static Map<String,String> ZoneIds = new HashMap<>();

    public static String txt2String(File file){
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
//                result.append(System.lineSeparator()).append(s);
                result.append(s);
            }
            br.close();
        }catch(Exception e){
            System.out.println("读取文件错误---->"+ e.getMessage());
        }
        return result.toString();
    }
}
