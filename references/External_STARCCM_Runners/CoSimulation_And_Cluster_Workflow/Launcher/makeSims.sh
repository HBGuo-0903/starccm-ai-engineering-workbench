#!/bin/bash
#https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/ReactivePipeMeshConv/makeSims.sh
cwd=$(pwd)

rm ReactivePipeMeshConv.sh*
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/ReactivePipeMeshConv/ReactivePipeMeshConv.sh

# Re 4808
TimeToRun_1080=2160
for iReynolds in 4808 1080 
do
    for iMesh in 16 8 4 2
    do
        for iCFL in 1 
        do
            for iKAdjustment in 1
            do
                TimeToRun=$(bc <<< "scale=2; $TimeToRun_1080 / $iReynolds")
                mkdir Rough/Re${iReynolds}/Mesh${iMesh}/CFL${iCFL}/k${iKAdjustment}/ -p
                
                cp Rough.sim Rough/Re${iReynolds}/Mesh${iMesh}/CFL${iCFL}/k${iKAdjustment}/
                cp ReactivePipeMeshConv.sh ${cwd}/Rough/Re${iReynolds}/Mesh${iMesh}/CFL${iCFL}/k${iKAdjustment}/

                cd "${cwd}/Rough/Re${iReynolds}/Mesh${iMesh}/CFL${iCFL}/k${iKAdjustment}/" 
                sed "s/__iMeshSize__/${iMesh}/g" -i ReactivePipeMeshConv.sh 
                sed "s/__iReynolds__/${iReynolds}/g" -i ReactivePipeMeshConv.sh 
                sed "s/__iTimeToRun__/${TimeToRun}/g" -i ReactivePipeMeshConv.sh 
                sed "s/__iReactivityAdjustment__/${iKAdjustment}/g" -i ReactivePipeMeshConv.sh 
                sed "s/__iTargetCourant__/${iCFL}/g" -i ReactivePipeMeshConv.sh 

                
                chmod 754 ReactivePipeMeshConv.sh    
                
                
                ./ReactivePipeMeshConv.sh
                cd "${cwd}"
            done
        done
    done
done
