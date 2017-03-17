#!/usr/bin/env bash
this=$( cd -P "$( dirname "${BASH_SOURCE[0]}" )/" && pwd -P )
. ${this}/global_config.sh

function run_algorithm() {
    if [ -f $1 ]; then
        bench_name=`basename $1`
        run_benchmark $1 > ${LOG_DIR}/run.log 2>&1
        if [ $? = "0" ]; then
             echo "${bench_name}  pass"
        else
             echo "${bench_name}  fail"            
        fi
    else
        echo "$1 isn't file or doesn't exist!"
    fi
}

function run {
    case $1 in
    "-f")
        if [ "$3" == "-s" ]; then
            GEN_DATA="no"
        fi
        run_algorithm $2
        ;;
    "")
        echo "sb"
        for file in ${BENCH_HOME}/algorithm_config/*; do
            run_algorithm $file
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


case $1 in
"run")
    shift
    run $@
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
