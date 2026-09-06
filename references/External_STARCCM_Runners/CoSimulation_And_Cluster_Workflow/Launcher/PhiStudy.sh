#wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/phiTimeSeries/PhiStudy.sh
RPM=500
MeshSize=5
FlowRateMlMin=8.3
RotorDiameter=80e-3
concentrationNa2SO4=0.00113730770584103
concentrationBaCl2=0.000835878753109641
TurbulentSchmidtNumber=0.75
Temperature=22

macroFileName="PhiStudy.java"

rm *.java
rm *.slurm.*
rm libuser.so
mkdir -p Results
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/phiTimeSeries/$macroFileName
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/phiTimeSeries/phiStudy.slurm.unix3
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/phiTimeSeries/phiStudy.slurm.xeon40
wget https://github.com/jrbentzon/starccm-scale-thermodynamics/releases/download/v0.3.3/libuser.so

cp libuser.so Results/

sed "s/__RPM__/$RPM/" -i $macroFileName
sed "s/__MeshSize__/$MeshSize/" -i $macroFileName
sed "s/__FlowRateMlMin__/$FlowRateMlMin/" -i $macroFileName
sed "s/__RotorDiameter__/$RotorDiameter/" -i $macroFileName
sed "s/__concentrationNa2SO4__/$concentrationNa2SO4/" -i $macroFileName
sed "s/__concentrationBaCl2__/$concentrationBaCl2/" -i $macroFileName
sed "s/__TurbulentSchmidtNumber__/$TurbulentSchmidtNumber/" -i $macroFileName
sed "s/__Temperature__/$Temperature/" -i $macroFileName
