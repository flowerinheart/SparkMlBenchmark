#!/bin/bash
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

bash ${bin}/run-MKL.sh --WRAPPER
bash ${bin}/run-MKL.sh --MKL --WRAPPER
