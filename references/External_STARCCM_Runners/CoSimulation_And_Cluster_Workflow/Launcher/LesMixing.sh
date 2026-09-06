RPM=__iRPM__
MeshSize=5
FlowRateMlMin=16.6
concentrationNa2SO4=1.19e-2
TurbulentSchmidtNumber=0.75

rm *.java
rm run*.slurm
mkdir -p Results
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/MixingLes/CouetteMixingStudyLes.java
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/MixingLes/RecordTurbulenceChemStats.java
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/MixingLes/runUnix3.slurm
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/MixingLes/runXeon40.slurm

cp RecordTurbulenceChemStats.java Results/

sed "s/__RPM__/$RPM/" -i CouetteMixingStudyLes.java
sed "s/__MeshSize__/$MeshSize/" -i CouetteMixingStudyLes.java
sed "s/__FlowRateMlMin__/$FlowRateMlMin/" -i CouetteMixingStudyLes.java
sed "s/__concentrationNa2SO4__/$concentrationNa2SO4/" -i CouetteMixingStudyLes.java
sed "s/__TurbulentSchmidtNumber__/$TurbulentSchmidtNumber/" -i CouetteMixingStudyLes.java