#!/usr/bin/env bash
this=$( cd -P "$( dirname "${BASH_SOURCE[0]}" )/" && pwd -P )
[ -z "$BENCH_HOME" ] && export BENCH_HOME="${this}/.."
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
