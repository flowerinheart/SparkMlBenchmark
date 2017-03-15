#!/usr/bin/env bash
this=$( cd -P "$( dirname "${BASH_SOURCE[0]}" )/" && pwd -P )
. ${this}/global_config.sh

function run {
    case $1 in
    "-f")
        if [ "$3" == "-s" ]; then
            GEN_DATA="no"
        fi
        run_benchmark $2
        ;;
    "")
        for file in ${BENCH_HOME}/env/*; do
            if [ -f $file ]; then
                bench_name=`basename $file`
                run_benchmark $file > ${BENCH_HOME}/logs/temp 2>&1
                if [ $? = "0" ]; then
                    echo "${bench_name}  pass"
                else
                    echo "${bench_name}  fail"
                    exit 255
                fi
            fi
        done
        ;;
    *)
        echo "You can use sparkbm like"
        echo "sparkbm run -h for help"
        echo "sparkbm run                                  # run all algorithm define in env dir"
        echo "sparkbm run -f [benchmark env file]          # run algorithm according env file and generate data every time"
        echo "sparkbm run -f [benchmark env file] -s          # run algorithm according env file and don't generate data every time"

        ;;


    esac
}

function clean {
    case $1 in
    "result")
        case $2 in
        "")
            for file in ${BENCH_HOME}/result/*;do
                rm $file
            done
            ;;
        *)
            rm "${BENCH_HOME}/${2}.csv"
            ;;
        esac
        ;;
    "data")
        . $2
        delete_dir "$DATA_DIR"
        ;;
    *)
        echo "You can use sparkbm clean like"
        echo "sparkbm clean -h for help"
        echo "sparkbm clean                                  # clean all file in data and result dir"
        echo "sparkbm clean data [benchmark name]            # clean data dir(when no benchmark name) or clean benchmark dir under data dir"
        echo "sparkbm clean result [benchmark name]          # clean result file(when no benchmark name) or clean benchmark.csv file under result dir"
        ;;
    esac
}



case $1 in
"run")
    shift
    run $@
    ;;
"clean")
    shift
    clean $@
    ;;
"build")
    cd ${BENCH_HOME}/benchmarks
    mvn clean package
#    REMOTE=`dirname ${REMOTE_JAR}` 
#    upload_jar "${LOCAL_JAR}" "${REMOTE}"
    cd ..
   
    ;;
*)
    echo "You can use sparkbm like"
    echo "sparkbm run -h                     # get help for run benchmark"
    echo "sparkbm clean -h                   # get help for clean"
    ;;
esac
