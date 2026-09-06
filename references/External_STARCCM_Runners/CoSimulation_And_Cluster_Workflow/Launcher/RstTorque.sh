# wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/TorqueRst/RstTorque.sh

RPM=__iRPM__
MeshSize=5
FlowRateMlMin=16.6
RotorDiameter=80e-3
RstC1e=1.44
RstC2e=2.4
RstCs=0.21


rm *.java
rm *.slurm
mkdir -p Results
wget -q https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/TorqueRst/CouetteCell.java
wget -q https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/TorqueRst/runXeon40.slurm
wget -q https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/TorqueRst/runXeon16.slurm
wget -q https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/TorqueRst/runUnix3.slurm

sed "s/__RPM__/$RPM/" -i CouetteCell.java
sed "s/__MeshSize__/$MeshSize/" -i CouetteCell.java
sed "s/__FlowRateMlMin__/$FlowRateMlMin/" -i CouetteCell.java
sed "s/__RotorDiameter__/$RotorDiameter/" -i CouetteCell.java

sed "s/__RstC1e__/$RstC1e/" -i CouetteCell.java
sed "s/__RstCs__/$RstCs/" -i CouetteCell.java
sed "s/__RstC2e__/$RstC2e/" -i CouetteCell.java