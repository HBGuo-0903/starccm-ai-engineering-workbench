#!/bin/bash
#SBATCH --no-requeue
#SBATCH --nodes=1
#SBATCH --ntasks-per-node=32
#SBATCH --time=12:00:00
#SBATCH --output=mpi_job_slurm.log
#SBATCH --partition=Unix3

cd $SLURM_SUBMIT_DIR

[ -e machine_list.txt ] && rm machine_list.txt
PROCS_PER_NODE=$(($SLURM_NPROCS/$SLURM_NNODES))
export CORENUM=($(scontrol show hostnames $SLURM_JOB_NODELIST))

for i in "${CORENUM[@]}"
do
  for j in $(seq 1 $PROCS_PER_NODE)
  do
   echo $i >> machine_list.txt
  done
done

# Remove old abort files
rm ABORT

export CDLMD_LICENSE_FILE=1999@flex.cd-adapco.com

export EXE="$(cat ~/.bashrc | grep ccm= | sed 's%alias ccm=\"%%g' | tr " " "\n" | head -1)"

# Get the last modified .sim file in the folder
FILENAME="$(ls *.sim -t | head -1 | xargs -n 1 basename)"
export FILENAME

# Find free port for StarCCM Server
PORT_STARCCM=47827
while netstat -atn | grep -q :$PORT_STARCCM; do
    PORT_STARCCM=$(expr $PORT_STARCCM + 1)
done

mkdir $SLURM_JOB_ID

# Setup the Server
$EXE -server -collab -port $PORT_STARCCM -power -podkey $LM_PROJECT -rsh ssh -np $SLURM_NPROCS -machinefile machine_list.txt $FILENAME > $SLURM_JOB_ID/stdout_server 2> $SLURM_JOB_ID/stderr_server &

# Wait until Server runs
until netstat -lnt | grep -q :$PORT_STARCCM; do
    sleep 5
done

# Download ExportShearStressScene.java
until ls -lah | grep -q ExportShearStressScene.java; do
    wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/postProcess/ExportShearStressScene.java
done

$EXE -batch ExportShearStressScene.java -port $PORT_STARCCM -host localhost > $SLURM_JOB_ID/stdout 2> $SLURM_JOB_ID/stderr