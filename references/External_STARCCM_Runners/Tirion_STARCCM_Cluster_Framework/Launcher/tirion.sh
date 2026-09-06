#!/bin/bash
#STATUS: [BASE]

###### DO NOT CHANGE ANYTHING UNLESS APPROVED ######
# Status will be changed to [META-MODIFIED] in the temporary file
# This script is used as a base and is meta-modified before job submission

# Slurm arguments [to be parsed] ({?} to be replaces with actual values)
#SBATCH -J {?}
#SBATCH --nodes={?}
#SBATCH --ntasks-per-node={?}
#SBATCH -o {?}./%j.out
#SBATCH -p Formula_Student_Team_Delft
#TEMPBATCH -w cn22

# Tirion framework... Made for Formula Student Team Delft.
# TODO: Future to be changed with a fully functional Web UI.
# Default output is in the current folder

power_on_demand_license="${STAR_POWER_ON_DEMAND_LIC:-REPLACE_WITH_LICENSE_KEY}";
export CDLMD_LICENSE_FILE="1999@flex.cd-adapco.com"
export nodelist=""

iterations=3500

function cleanup {                                                                                                                                                   
   echo "[!] Tirion framework exiting..."
   exit 0
}

function finishedJob {

    if [ -n "$SLURM_NTASKS" ]; then
        echo "[-] Moving simmed file."
        rm $nodelist
        ssh head01 "mv $simPath@0$iterations /shared_scratch_volume/Formula_Student_Team_Delft/simulations/$USER/"
        echo "[*] Moved simmed files to /shared_scratch_volume/Formula_Student_Team_Delft/simulations/$USER/"
    fi

    echo "[*] Exited gracefully."
    exit 0
}

function initSLURMenv {
    # ... Load other modules here
    module load dc-star-ccm+/14.04.013
    export CDLMD_LICENSE_FILE="1999@${LICENSE_SERVER_HOST}"
    nodelist="./temp/slurmhosts.$SLURM_JOB_ID.txt"
    echo "[-] Building a machine file to $nodelist."
    srun hostname -s &> $nodelist
}

trap cleanup 2;
trap finishedJob 0;

if [ -n "$SLURM_NTASKS" ]; then
    initSLURMenv
fi

# {?} will be replaced with actual values.
macrosPath={?};
simPath={?};
cores="$SLURM_NTASKS";
starccm="starccm+";
# starccm="/opt/CD-adapco/STAR-CCM+11.04.012-R8/star/bin/starccm+"

[ $macrosPath == {?} ] && echo "Not designed to be run like this." && exit 0
[ $simPath == {?} ] && echo "Not designed to be run like this." && exit 0

function main {
    echo "[-] Initializing Tirion framework...";
    
    sleep 1;

    if [ ! -n "$cores" ]; then
        cores=$(( $(grep -c ^processor /proc/cpuinfo) / 2 ));
        echo "No amount of cores specified, using half of all the cores by default.";
        echo "You can specify the amount of cores by adding an additional parameter after the .sim file";
    fi
    
    echo "[*] Initialized."
    echo "[*] Loaded:"
    echo "    -- Simulation File: $simPath"
    echo "    -- Macro Script: $macrosPath"
    echo "[%] Running STAR-CCM+ with $cores cores configured..."

    sleep 1

    if [ -z $SLURM_NTASKS ]; then 
        echo "[-] Running on a local cluster..." 
        $starccm\
            -jvmargs '-server'\
            -rsh ssh\
            -np $cores\
            -podkey $power_on_demand_license\
            -licpath $CDLMD_LICENSE_FILE\
            -power $simPath\
            -hardwarebatch\
            -batch $macrosPath
    else
        echo "[-] Running in a SLURM Environment Cluster..." 
        $starccm\
             -jvmargs '-server'\
             -tokensonly\
             -rsh ssh\
             -mpi intel\
             -np $cores\
             -machinefile $nodelist\
             -batch $macrosPath\
             $simPath
    fi

    # WIP
    # convert_to_videos
}

# WIP
function convert_to_videos {
    currentDir=$(pwd);
    cd $(dirname $simPath);
    
    simName=$(basename $simPath);
    simName=${simName::-4};

    cd "PostProcessing#${simName}";

    for f in *; do
        if [ -d "$f" ]; then
            cd $f;
            bash ./src/utils/video_output.sh '$f_*';
            echo $f;
            cd ..;
        fi
    done
}

main


