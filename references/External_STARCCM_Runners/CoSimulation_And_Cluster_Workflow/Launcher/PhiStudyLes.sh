#wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/phiTimeSeriesLes/PhiStudyLes.sh
concentrationNa2SO4=0.00113730770584103
concentrationBaCl2=0.000835878753109641
TurbulentSchmidtNumber=0.75
Temperature=22
SimulationTime=2700
k=__k__


macroFileName="PhiStudyLes.java"

rm *.java
rm *.slurm.*
rm libuser.so
wget -q https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/phiTimeSeriesLes/$macroFileName
wget -q https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/phiTimeSeriesLes/phiStudyLes.slurm.unix3
wget -q https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/phiTimeSeriesLes/phiStudyLes.slurm.xeon40
wget -q https://github.com/jrbentzon/starccm-scale-thermodynamics/releases/download/v0.3.3/libuser.so

sed "s/__concentrationNa2SO4__/$concentrationNa2SO4/" -i $macroFileName
sed "s/__concentrationBaCl2__/$concentrationBaCl2/" -i $macroFileName
sed "s/__TurbulentSchmidtNumber__/$TurbulentSchmidtNumber/" -i $macroFileName
sed "s/__Temperature__/$Temperature/" -i $macroFileName
sed "s/__SimulationTime__/$SimulationTime/" -i $macroFileName
sed "s/__RateConstant__/$k/" -i $macroFileName