#!/bin/bash
#wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/ReactivePipeMeshConv/ReactivePipeMeshConv.sh

Reynolds=__iReynolds__
MeshSize=__iMeshSize__
TimeToRun=__iTimeToRun__
ReactiveKAdjustment=__iReactivityAdjustment__
TargetCourant=__iTargetCourant__


rm *.java
rm run*.slurm
rm libuser.so
mkdir -p Results
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/ReactivePipeMeshConv/MeshConvergence.java
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/ReactivePipeMeshConv/runUnix3.slurm
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/ReactivePipeMeshConv/runXeon8.slurm
wget https://github.com/jrbentzon/starccm-scale-thermodynamics/releases/download/v0.3.3/libuser.so

cp libuser.so Results/

sed "s/__Reynolds__/${Reynolds}/" -i MeshConvergence.java
sed "s/__MeshSize__/${MeshSize}/" -i MeshConvergence.java
sed "s/__TimeToRun__/${TimeToRun}/" -i MeshConvergence.java
sed "s/__reactivityAdjustment__/${ReactiveKAdjustment}/" -i MeshConvergence.java
sed "s/__targetCourant__/${TargetCourant}/" -i MeshConvergence.java
