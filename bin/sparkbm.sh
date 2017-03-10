#!/usr/bin/env bash
this=$( cd -P "$( dirname "${BASH_SOURCE[0]}" )/" && pwd -P )
[ -z "$BENCH_HOME" ] && export BENCH_HOME="${this}/.."

function run {
    case $1 in
    "-f")
        run_benchmark $2
        ;;
    "")
        for file in ${BENCH_HOME}/env/*; do
            if [ -f $file ]; then
                run_benchmark $file
            fi
        done
        ;;
    *)
        echo "Unknown run opt"
        echo "You can use sparkbm like"
        echo "sparkbm run -h for help"
        echo "sparkbm run                                  # run all algorithm define in env dir"
        echo "sparkbm run -f [benchmark env file]          # run algorithm according env file"

        ;;


    esac
}

function clean {
    case $1 in
    "result")
        case $2 in
        "")
            rm "${BENCH_HOME}/result/*"
            ;;
        *)
            rm "${BENCH_HOME}/${2}.csv"
            ;;
        esac
        ;;
    "data")
        case $2 in
        "")
            rm -r "${BENCH_HOME}/data/*"
            ;;
        *)
            rm -r "${BENCH_HOME}/data/$2"
            ;;
        esac
        ;;
    "")
        rm -r "${BENCH_HOME}/data/*"
        rm -r "${BENCH_HOME}/result/*"
        ;;
    *)
        echo "Unknown clean object"
        echo "You can use sparkbm like"
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
    cd ..
    ;;
*)
    echo "Unknown option!"
    echo "sparkbm run -h                     # get help for run benchmark"
    echo "sparkbm clean -h                   # get help for clean"
    ;;
esac
