package skydata.spark.benchmark.metrics;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by Darnell on 2016/12/1.
 */

class Stat<T> {
    public Class<T> classInfo;
    public T value;
    public String formatStr;
    private BiFunction<T, String, String> handle = (T v, String f) -> String.format(formatStr, value);

    public Stat(T val, String str, Class<T> info) {
        value = val;
        formatStr = str;
        classInfo = info;
    }

    public Stat(T val, String str, Class<T> info, BiFunction<T, String, String> h) {
        value = val;
        formatStr = str;
        handle = h;
        classInfo = info;
    }


    public void update(Function<T, T> func) {
        value = func.apply(value);
    }


    public String toString() {
        return StrTools.strip(handle.apply(value, formatStr));
    }
}

public abstract class JsonEstimator {


    protected HashMap<String, HashMap<String, Stat>> valInfo = new LinkedHashMap<>();
    protected String statName = "";

    abstract public void estimator(File file) throws ParseException, IOException;

    public void show(PrintStream out) {
        printValTable(out, this, 3);
    }

    static public void printValTable(PrintStream out, JsonEstimator estimator, int vlen) {
        HashMap<String, HashMap<String, Stat>> tables = estimator.valInfo;
        String interval = StrTools.genRep(" ", vlen);
        String firstCol = "TableName";
        BiFunction<String, String, Integer> cellLen = (String a, String b) -> {
            return Math.max(a.length(), b.length()) + 2 * vlen;
        };
        int maxLen = 0;
        ArrayList<StringBuilder> rows = new ArrayList<>();
        for (String tableName : tables.keySet()) {
            //gen first Col
            HashMap<String, Stat> table = tables.get(tableName);
            int num = 0, n = table.size();
            StringBuilder headBuilder = null;
            StringBuilder contentBuilder = null;
            //get every col
            for (String statName : table.keySet()) {
                if (num == 0) {
                    headBuilder = new StringBuilder("|");
                    contentBuilder = new StringBuilder("|");
                }

                statName = StrTools.strip(statName);
                String statStr = table.get(statName).toString();
                int len = cellLen.apply(statName, statStr);
                headBuilder.append(StrTools.center(statName, len) + interval + "|");
                contentBuilder.append(StrTools.center(statStr, len) + interval + "|");
                if (num == 2) {
                    len = headBuilder.length();
                    maxLen = Math.max(len, maxLen);
                    StringBuilder tableLine = new StringBuilder(StrTools.genRep("-", len));
                    rows.add(tableLine);
                    rows.add(headBuilder);
                    rows.add(tableLine);
                    rows.add(contentBuilder);
                    rows.add(tableLine);
                }
                num = (num + 1) % 3;

            }
        }


        int finalMaxLen = maxLen;
        BiConsumer<StringBuilder, ArrayList<StringBuilder>> combiner = (StringBuilder lres, ArrayList<StringBuilder> lrows) -> {
            for (StringBuilder row : lrows) {
                int len = row.length();
                String head = "*" + interval;
                String tail = StrTools.genRep(" ", finalMaxLen - len) + interval + "*";
                lres.append("\n").append(head).append(row).append(tail);
            }
        };
        String tailLine = StrTools.genRep("*", maxLen + 2 * (vlen + 1));
        String nonLine = "*" + StrTools.genRep(" ", maxLen + 2 * vlen) + "*";
        String title = StrTools.center(estimator.statName, maxLen + 2 * vlen, '*');


        StringBuilder res = new StringBuilder();
        StrTools.appendLine(res, title);
        StrTools.appendLine(res, nonLine, vlen);
        combiner.accept(res, rows);
        StrTools.appendLine(res, nonLine, vlen);
        StrTools.appendLine(res, tailLine);

        System.out.println(res);

    }
}
