RPM=__iRPM__
MeshSize=5
FlowRateMlMin=16.6
concentrationNa2SO4=1.19e-2
TurbulentSchmidtNumber=0.75

rm *.java
rm run*.slurm
mkdir -p Results
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/MixingRst/CouetteMixingStudyRst.java
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/MixingRst/runUnix3.slurm
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/MixingRst/runXeon40.slurm


sed "s/__RPM__/$RPM/" -i CouetteMixingStudyRst.java
sed "s/__MeshSize__/$MeshSize/" -i CouetteMixingStudyRst.java
sed "s/__FlowRateMlMin__/$FlowRateMlMin/" -i CouetteMixingStudyRst.java
sed "s/__concentrationNa2SO4__/$concentrationNa2SO4/" -i CouetteMixingStudyRst.java
sed "s/__TurbulentSchmidtNumber__/$TurbulentSchmidtNumber/" -i CouetteMixingStudyRst.java