#!/cm/shared/apps/STAR-CCM+2019.2.1/14.04.013/STAR-CCM+14.04.013/star/bin/python3

import argparse
import subprocess
import time
import re
import os
import atexit
import signal

parser = argparse.ArgumentParser(prog='Tirion', description='A framework for STARCCM+ CFD Processing. Written for Formula Student Team Delft.')

parser.add_argument('--name', metavar="-j", type=str, help='Job name.', required=True)
parser.add_argument('--simulation', metavar="-s", type=str, help='Path to the .sim file to run processing on.', required=True)
parser.add_argument('--nodes', metavar='-n', default=1, type=int, help='Number of nodes to request the resource management system for.', required=True)
parser.add_argument('--cores', metavar='-c', type=int, help='Number of cores per node.', required=True)
parser.add_argument('--temp', default=0, type=int, help="Should the generated temp job.sh file be saved")
parser.add_argument('--pp', default=0, type=int, help="Just post-process.")
parser.add_argument('--meshing', default=0, type=int, help="Just mesh.")
parser.add_argument('--full', default=1, type=int, help="Use the full package simulation, postprocessing, exporting (wip, exporting is not working yet).")
parser.add_argument('--log', metavar='-l', default="", type=str, help="Path to which to save the logs from SLURM.")

# WIP
#(iterations, !symmetry, types, output) TODO: Merge mesh and pp scripts
# parser.add_argument('--cornering', metavar='-cn', type=int, default=0, help='[WIP] A flag to enable cornering on processing.')
# parser.add_argument('--iterations', metavar="-i", default=3000, type=int, help='[WIP] Number of iterations to run the simulation for.')
# parser.add_argument('--output', metavar='-o', type=str, help='[WIP] Ouptut directory')
# parser.add_argument('--symmetry', type=bool, help='[!stub(wip)] A flag to enable symmetry. If cornering is enabled this doesn\'t matter')
# parser.add_argument('--type', metavar='-t', type=str, help='[WIP] Type of processing: meshing | simulation | post-processing. Default is all.[!stub(wip)]')
args = parser.parse_args()

# file_name = './temp/tirion.job.%s.sh'%(int(time.time()))
file_name = './temp/tirion.job.%s.sh'%(args.name)

@atexit.register
def cleanup():
    if args.temp == 1:
        os.remove(file_name)

signal.signal(signal.SIGINT, cleanup)
signal.signal(signal.SIGTERM, cleanup)
signal.signal(signal.SIGQUIT, cleanup)

#The idea is to parse these and change .sh file, which you can open
script = open('./tirion.sh','r')
line_list = script.readlines()
script.close()

# Change the arguments
for i, line in enumerate(line_list):
    # Parse for the macro directory, job name, etc
    if re.match("#STATUS", line) is not None:
        line_list[i] = line.replace("[BASE]", "[META-MODIFIED]")
    
    if re.match("#SBATCH -J", line) is not None:
        line_list[i] = line.replace("{?}" ,args.name)
        
    if re.match("#SBATCH --nodes", line) is not None:
        line_list[i] = line.replace("{?}", str(args.nodes))
        
    if re.match("#SBATCH --ntasks-per-node=", line) is not None:
        line_list[i] = line.replace("{?}", str(args.cores))
    
    if re.match("#SBATCH -o", line) is not None:
        line_list[i] = line.replace("{?}", args.log)

    # Parse for macroPath, Simulation directory
    if re.match("macrosPath=", line) is not None:
        if args.pp == 1:
            print("Post-processing...")
            macros = './src/main/PostProcessing.java'
        elif args.meshing == 1:
            print("Running simulation.")
            macros = './src/main/MainMacro.java'
        elif args.full == 1:
            print("Going full core.")
            macros = "./src/main/FullCore.java"
        else:
            print("[DEFAULT]: Everything is running.")
            macros = "./src/main/MainMacro.java, ./src/main/PostProcessing.java"
        
        line_list[i] = line.replace("{?}", macros)
    
    if re.match("simPath=", line) is not None:
        line_list[i] = line.replace("{?}", args.simulation)
        

file_to_write = open(file_name, 'w')
file_to_write.writelines(line_list)
file_to_write.close()

subprocess.call("sbatch %s" % (file_name), shell=True)
