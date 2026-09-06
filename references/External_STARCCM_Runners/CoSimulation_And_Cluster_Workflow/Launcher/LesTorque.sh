# wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/TorqueLes2/LesTorque.sh
# This runs LES on top of existing RANS .sim file (picks latest modified .sim file in folder)


RPM=__iRPM__
MeshSize=5
FlowRateMlMin=16.6
RotorDiameter=80e-3

rm *.java
rm *.slurm
mkdir -p Results
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/TorqueLes2/LesTorque.java
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/TorqueLes2/RecordReynoldsStresses.java
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/TorqueLes2/RecordMeanShearStress.java

wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/TorqueLes2/ExportVelocity.java
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/TorqueLes2/ExportShearStressScene.java

wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/TorqueLes2/runXeon8.slurm
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/TorqueLes2/runXeon16.slurm
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/TorqueLes2/runXeon40.slurm

wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/TorqueLes2/recordShear.slurm

cp RecordReynoldsStresses.java Results/
cp RecordMeanShearStress.java Results/
cp ExportVelocity.java Results/
cp ExportShearStressScene.java Results/
mv recordShear.slurm Results/

sed "s/__RPM__/$RPM/" -i LesTorque.java
sed "s/__MeshSize__/$MeshSize/" -i LesTorque.java
sed "s/__FlowRateMlMin__/$FlowRateMlMin/" -i LesTorque.java
sed "s/__RotorDiameter__/$RotorDiameter/" -i LesTorque.java