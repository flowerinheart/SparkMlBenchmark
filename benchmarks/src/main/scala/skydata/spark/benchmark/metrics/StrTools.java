package skydata.spark.benchmark.metrics;

import org.json.simple.JSONObject;

import java.io.File;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Created by Darnell on 2016/12/1.
 */
public class StrTools {
    public static String genPath(String ...p){
        StringBuilder b = new StringBuilder(".");
        for(String str : p)
            b.append(File.separatorChar).append(str);
        return b.toString();
    }
    public static String center(String s, int size) {
        return center(s, size, ' ');
    }

    public static String strip(String s){
        return strip(s, ' ');
    }

    public static String strip(String s, char c){
        int n = s.length();
        int b = 0, e = n - 1;
        for(b = 0; b < n; b++){
            if(s.charAt(b) != c)
                break;
        }
        if(b == n) return "";
        for(e = n - 1; e >= 0; e--){
            if(s.charAt(e) != c)
                break;
        }
        return s.substring(b, e + 1);
    }

    public static void appendLine(StringBuilder source, String str, int times){
        for(int i = 0; i < times; i++)
            source.append("\n").append(str);
    }
    public static void insertLine(StringBuilder source, int index, String str, int times){
        for(int i = 0; i < times; i++){
            source.insert(index, "\n");
            index++;
            source.insert(index, str);
            index += str.length();
        }
    }
    public static void insertLine(StringBuilder source, int index, String ...args){
        for(String build : args){
            source.insert(index, "\n");
            index++;
            source.insert(index, build);
            index += build.length();
        }
    }
    public static void  appendLine(StringBuilder source, String ...args){
        for(String build : args){
            source.append("\n").append(build);
        }
    }
    public static String center(String s, int size, char pad) {
        if (s == null || size <= s.length())
            return s;

        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < (size - s.length()) / 2; i++) {
            sb.append(pad);
        }
        sb.append(s);
        while (sb.length() < size) {
            sb.append(pad);
        }
        return sb.toString();
    }
    public static void printTable(int length, BooleanSupplier supplier) {
        System.out.println(genRep("-", length));
        supplier.getAsBoolean();
        System.out.printf("%s\n\n\b", genRep("-", length));
    }

    public static void printTable(String[] params) {

    }

    public static JSONObject extract(JSONObject json, String name, Consumer<JSONObject> handler) {
        if (json == null) return null;
        JSONObject res = (JSONObject) json.get(name);
        if (res == null) return res;
        handler.accept(res);
        return res;
    }

    public static String genFormatStr(String format, int interval, int length, String dem) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < interval; i++)
            builder.append(" ");
        String intervalStr = builder.toString();
        builder.delete(0, builder.length());
        for (int i = 0; i < length; i++)
            builder.append(intervalStr).append(format).append(intervalStr).append(dem);
        return builder.toString();
    }

    public static String genFormatStr(String format, int length) {
        return genFormatStr(format, 3, length, "|");
    }

    public static String genRep(String format, int num) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < num; i++)
            builder.append(format);
        return builder.toString();
    }
}
