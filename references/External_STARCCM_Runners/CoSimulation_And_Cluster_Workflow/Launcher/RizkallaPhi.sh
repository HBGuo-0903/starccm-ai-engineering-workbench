#wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/RizkallaPhi/RizkallaPhi.sh; chmod 700 RizkallaPhi.sh;
concentrationNa2SO4=0.0004
concentrationBaCl2=0.0028
TurbulentSchmidtNumber=0.75
Temperature=22
SimulationTime=10

macroFileName="RizkallaPhi.java"

rm *.java >> prep.log
rm *.slurm.*  >> prep.log
rm *.so >> prep.log
wget -q https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/RizkallaPhi/$macroFileName
wget -q https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/RizkallaPhi/rizkallaPhi.slurm.unix3
wget -q https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/RizkallaPhi/rizkallaPhi.slurm.xeon40
wget -q https://github.com/jrbentzon/starccm-scale-thermodynamics/releases/download/v0.3.3/libuser.so

sed "s/__concentrationNa2SO4__/$concentrationNa2SO4/" -i $macroFileName
sed "s/__concentrationBaCl2__/$concentrationBaCl2/" -i $macroFileName
sed "s/__TurbulentSchmidtNumber__/$TurbulentSchmidtNumber/" -i $macroFileName
sed "s/__Temperature__/$Temperature/" -i $macroFileName
sed "s/__SimulationTime__/$SimulationTime/" -i $macroFileName
