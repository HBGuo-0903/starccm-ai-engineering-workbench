#!/bin/bash
#https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/ReactivePipeMeshConv/runSims.sh
cwd=$(pwd)



for iReynolds in 4808 1080 
do
    for iMesh in 16 8 4 2
    do
        for iCFL in 1 
        do
            for iKAdjustment in 1
            do
                cd "${cwd}/Rough/Re${iReynolds}/Mesh${iMesh}/CFL${iCFL}/k${iKAdjustment}/" 
                sbatch runUnix3.slurm
                cd "${cwd}"
            done
        done
    done
done