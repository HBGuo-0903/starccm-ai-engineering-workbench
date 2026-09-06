RPM=500
MeshSize=20
FlowRateMlMin=8.3
concentrationNa2SO4=1.14e-3
concentrationBaCl2=2.27e-4
TurbulentSchmidtNumber=0.75
Temperature=22

rm *.java
rm run*.slurm
rm libuser.so
mkdir -p Results
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/ReactiveRst/CouetteReactiveStudyRst.java
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/ReactiveRst/runUnix3.slurm
wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/ReactiveRst/runXeon40.slurm
wget https://github.com/jrbentzon/starccm-scale-thermodynamics/releases/download/v0.3.3/libuser.so

cp libuser.so Results/

sed "s/__RPM__/$RPM/" -i CouetteReactiveStudyRst.java
sed "s/__MeshSize__/$MeshSize/" -i CouetteReactiveStudyRst.java
sed "s/__FlowRateMlMin__/$FlowRateMlMin/" -i CouetteReactiveStudyRst.java
sed "s/__concentrationNa2SO4__/$concentrationNa2SO4/" -i CouetteReactiveStudyRst.java
sed "s/__concentrationBaCl2__/$concentrationBaCl2/" -i CouetteReactiveStudyRst.java
sed "s/__TurbulentSchmidtNumber__/$TurbulentSchmidtNumber/" -i CouetteReactiveStudyRst.java
sed "s/__Temperature__/$Temperature/" -i CouetteReactiveStudyRst.java
