package skydata.spark.benchmark.metrics;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by weijia.liu
 * Date :  2016/12/19.
 * Time :  12:34
 */
public class ResultStatParser {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.print("please input the result stat file path.");
            System.exit(1);
        }

        File file = new File(args[0]);
        if (!file.exists()) {
            System.out.printf("%s is not exist.\n", args[0]);
            System.exit(1);
        }

        List<String> lines = getFileLines(file);
        double trainingTime = getFieldAverage(lines, "trainingTime");
        double testTime = getFieldAverage(lines, "testTime");
        System.out.printf("%s : training time is : %f s, test time is %f s\n", file.getName(), trainingTime, testTime);
    }

    private static List<String> getFileLines(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    private static double getFieldAverage(List<String> lines, String fileName) {
        List<Double> trainingTimes = lines.stream().filter(s -> s != null && !s.isEmpty()).
                map(s -> JSON.parseObject(s).getDoubleValue(fileName)).
                sorted().collect(Collectors.toList());
        trainingTimes.remove(trainingTimes.size() - 1);
        trainingTimes.remove(0);
        return trainingTimes.stream().reduce(((d1, d2) -> d1 + d2)).get() / trainingTimes.size();
    }

}
