package skydata.spark.benchmark.metrics;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;

/**
 * Created by Darnell on 2016/12/1.
 */
public class SparkJsonEstimator extends JsonEstimator{
        private static void analysis(File file, ArrayList<SparkJsonEstimator> list){
            if(file.isFile()){
                SparkJsonEstimator estimator = new SparkJsonEstimator();
                try {
                    estimator.estimator(file);
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                list.add(estimator);
            }else {
                for (File t : file.listFiles()) {
                    analysis(t, list);
                }
            }
        };
    public static void main(String []args){
        run(args);
    }
    public static void run(String []args){
        if(args.length < 2) throw new RuntimeException(new IllegalArgumentException());

        boolean avg = args[0].equals("AVG");
        ArrayList<SparkJsonEstimator> estimators = new ArrayList<>();
        for(int i = 1; i < args.length; i++)
            analysis(new File(args[i]), estimators);
        if(avg) {
            SparkJsonEstimator stat = new SparkJsonEstimator(estimators);
            stat.show(System.out);
        }else{
            estimators.forEach((SparkJsonEstimator s) ->s.show(System.out));
        }
    }


    static private BiFunction<Long, String, String> byteFunc = (Long t, String str)->String.format(str, (double)(t >> 20), (double)(t >> 20) / 1024);
    static private BiFunction<Long, String, String> timeFunc = (Long t, String str)->String.format(str, (double)t / 1000, (double)(t) / 1000 / 60);
    static private String timeformat = "%.2fs, %.4fmin", byteformat = "%.2fMB, %.2fGB";
    private int appNum = 0;
    private String runMode = "";


    private void mergeEstimator(SparkJsonEstimator estimator) {
        estimator.valInfo.forEach((String tableName, HashMap<String, Stat> table) -> {
            if(!tableName.equals("MetaInfo")){
            HashMap<String, Stat> table2 = valInfo.get(tableName);
            table.forEach((String key, Stat stat) -> {
                if (!table2.containsKey(key))
                    return;
                Stat stat2 = table2.get(key);
                switch ((stat2.classInfo.getSimpleName())) {
                    case "long" : case "Long":
                        stat2.value = (long) stat.value + (long) stat2.value;
                        break;
                    case "String":
                        break;
                    case "Integer":
                        break;
                    default:
                        throw new RuntimeException(stat2.classInfo.getSimpleName() + " " + key);
                }
            });
            }
        });
    }


        private void avg(int n) {
            valInfo.forEach((String tableName, HashMap<String, Stat> table) -> {
                if (!tableName.equals("MetaInfo")) {
                    table.forEach((String key, Stat stat) -> {
                        switch (stat.classInfo.getSimpleName()) {
                            case "long": case "Long":
                                stat.value = (long) stat.value / n;
                                break;
                            case "String":
                                break;
                            case "Integer":
                                break;
                            default:
                                throw new RuntimeException("Error Stat Type");
                        }
                    });
                }
            });
        }



    public SparkJsonEstimator(List<SparkJsonEstimator> estimators){
        init(this);
        int n = estimators.size();
        boolean isDelete = n > 3;
        n = isDelete ? n - 2 : n;
        valInfo.get("MetaInfo").get("App Num").value = 5;
        statName = "Stat " + String.valueOf(n) + " App";
        if(isDelete) {
            // fix : java.lang.IndexOutOfBoundsException
            Collections.sort(estimators, (o1, o2) -> {
                long app1Time = (long) o1.valInfo.get("AppStatInfo").get("App Total Time").value;
                long app2Time = (long) o2.valInfo.get("AppStatInfo").get("App Total Time").value;
                return (int) (app1Time - app2Time);
            });

            estimators.remove(estimators.size() - 1);
            estimators.remove(0);
//            Long minTime = Long.MAX_VALUE, maxTime = Long.MIN_VALUE;
//            int minIndex = 0, maxIndex = 0, i = 0;
//            for (SparkJsonEstimator estimator : estimators) {
//                long appTime = (long) estimator.valInfo.get("AppStatInfo").get("App Total Time").value;
//                if (appTime < minTime) {
//                    minIndex = i;
//                    minTime = appTime;
//                }
//                if (appTime > maxTime) {
//                    maxIndex = i;
//                    maxTime = appTime;
//                }
//                i++;
//            }
//            estimators.remove(maxIndex);
//            estimators.remove(minIndex);
//            n -= 2;
        }
        estimators.forEach((SparkJsonEstimator t) -> mergeEstimator(t));
        avg(n);
        this.valInfo.get("AppStatInfo").put("App Name", estimators.get(0).valInfo.get("AppStatInfo").get("App Name"));
    }




    static private void init(SparkJsonEstimator estimator){
        HashMap<String, HashMap<String, Stat>> valInfo = estimator.valInfo;
        HashMap<String, Stat> metaInfo = new LinkedHashMap<>();
        valInfo.put("MetaInfo", metaInfo);
        metaInfo.put("App Num", new Stat<>(0, "%d", Integer.class));
        //app info
        HashMap<String, Stat> appInfo = new LinkedHashMap<>();
        valInfo.put("AppStatInfo", appInfo);
        appInfo.put("App Name", new Stat<>("", "%s", String.class));
        appInfo.put("App ID", new Stat<>("", "%s", String.class));
        appInfo.put("App Total Time", new Stat<>((long)0, timeformat, Long.class, timeFunc));
        appInfo.put("Max Gc Time", new Stat<>((long) 0, timeformat, Long.class, timeFunc));
        appInfo.put("Min Gc Time", new Stat<>((long) 0, timeformat, Long.class, timeFunc));
        appInfo.put("Gc Time", new Stat<>((long) 0, timeformat, Long.class, timeFunc));

        //stage info
        HashMap<String, Stat> stageInfo = new LinkedHashMap<>();
        valInfo.put("StageStatInfo", stageInfo);
        stageInfo.put("Stage Num", new Stat<>((long)0, "%d", Long.class));
        stageInfo.put("Stage Total Time", new Stat<>((long)0, timeformat, Long.class, timeFunc));

        //task info
        HashMap<String, Stat> taskInfo = new LinkedHashMap<>();
        valInfo.put("TaskStatInfo", taskInfo);
        taskInfo.put("Input Bytes", new Stat<>((long) 0, byteformat, Long.class, byteFunc));
        taskInfo.put("Task Num", new Stat<>((long) 0, "%d", Long.class));
        taskInfo.put("NoEmpty Task Num", new Stat<>((long) 0, "%d", Long.class));
        taskInfo.put("Result Task Num", new Stat<>((long) 0, "%d", Long.class));
        taskInfo.put("Result Task Time", new Stat<>((long) 0, timeformat,Long.class, timeFunc));
        taskInfo.put("Shuffle Task Num", new Stat<>((long) 0, "%d", Long.class));
        taskInfo.put("Shuffle Task Time", new Stat<>((long) 0, timeformat, Long.class, timeFunc));
        taskInfo.put("Shuffle Read", new Stat<>((long) 0, byteformat, Long.class,byteFunc));
        taskInfo.put("Shuffle Write", new Stat<>((long) 0, byteformat, Long.class, byteFunc));


    }


    public SparkJsonEstimator(){
        statName = "Single App";
        init(this);
        valInfo.get("MetaInfo").get("App Num").value = 1;
    }

    @Override
    public void estimator(File file) throws ParseException, IOException {
        HashMap<String, Stat> taskInfo = valInfo.get("TaskStatInfo");
        HashMap<String, Stat> stageInfo = valInfo.get("StageStatInfo");
        HashMap<String, Stat> appInfo = valInfo.get("AppStatInfo");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        JSONParser parser = new JSONParser();



        long appStartTime = 0, appEndTime = 0;
        long stageTime = 0, stageNum = 0;
        long resultTime = 0, resultNum = 0, shuffleTime = 0, shuffleNum = 0;
        long shuffleRead = 0, shuffleWrite = 0, gcTime = 0, inputBytes = 0, maxGc = 0, minGc = Long.MAX_VALUE;
        long taskNum = 0, nonEmptyTaskNum = 0;
        while ((line = br.readLine()) != null) {
            JSONObject jsonobject = (JSONObject) parser.parse(line);
            String event = (String) jsonobject.get("Event");
            switch (event) {
                case "SparkListenerApplicationStart": {
                    appStartTime = (long) jsonobject.get("Timestamp");
                    appInfo.get("App Name").update((Object v) -> (String) jsonobject.get("App Name"));
                    appInfo.get("App ID").update((Object v) -> (String) jsonobject.get("App ID"));
                    break;
                }
                case "SparkListenerApplicationEnd":
                    appEndTime = (long) jsonobject.get("Timestamp");
                    appInfo.get("App Total Time").value = appEndTime - appStartTime;
                    break;
                case "SparkListenerStageCompleted": {
                    JSONObject info = (JSONObject) jsonobject.get("Stage Info");
                    stageNum++;
                    stageTime += (Long) info.get("Submission Time") + (Long) info.get("Completion Time");
                    break;
                }
                case "SparkListenerTaskEnd": {
                    taskNum += 1;

                    String tasktype = (String) jsonobject.get("Task Type");
                    //parse Task Info
                    JSONObject taskInfoOjbect = (JSONObject) jsonobject.get("Task Info");
                    Long st = (Long) taskInfoOjbect.get("Launch Time");
                    Long ft = (Long) taskInfoOjbect.get("Finish Time");
                    if(ft > st)
                        nonEmptyTaskNum += 1;
                    if (tasktype.equals("ResultTask")) {
                        resultNum += 1;
                        resultTime += (ft - st);
                    } else if (tasktype.equals("ShuffleMapTask")) {
                        shuffleNum += 1;
                        shuffleTime += (ft - st);
                    }


                    //parse Task Metrics
                    JSONObject taskMetrics = (JSONObject) jsonobject.get("Task Metrics");
                    if (taskMetrics != null) {
                        shuffleRead += (long)((JSONObject) taskMetrics.get("Shuffle Read Metrics")).get("Remote Bytes Read");
                        shuffleRead += (long)((JSONObject) taskMetrics.get("Shuffle Read Metrics")).get("Local Bytes Read");
                        shuffleWrite += (long)((JSONObject) taskMetrics.get("Shuffle Write Metrics")).get("Shuffle Bytes Written");
                        long gc = (Long) taskMetrics.get("JVM GC Time");
                        gcTime += gc;
                        maxGc = Math.max(maxGc, gc);
                        minGc = Math.min(minGc, gc);

                        //parse Input Metrics
                        inputBytes += (long)((JSONObject) taskMetrics.get("Input Metrics")).get("Bytes Read");
                    }

                    break;
                }
            }
            stageInfo.get("Stage Num").value = stageNum;
            stageInfo.get("Stage Total Time").value = stageTime;
            taskInfo.get("Result Task Num").value = resultNum;
            taskInfo.get("Result Task Time").value = resultTime;
            taskInfo.get("Shuffle Task Num").value = shuffleNum;
            taskInfo.get("Shuffle Task Time").value = shuffleTime;

            appInfo.get("Gc Time").value = gcTime;
            appInfo.get("Max Gc Time").value = maxGc;
            appInfo.get("Min Gc Time").value = minGc;
            taskInfo.get("Shuffle Read").value = shuffleRead;
            taskInfo.get("Shuffle Write").value = shuffleWrite;
            taskInfo.get("Input Bytes").value = inputBytes;
            taskInfo.get("Task Num").value = taskNum;
            taskInfo.get("NoEmpty Task Num").value = nonEmptyTaskNum;
        }
    }


}
