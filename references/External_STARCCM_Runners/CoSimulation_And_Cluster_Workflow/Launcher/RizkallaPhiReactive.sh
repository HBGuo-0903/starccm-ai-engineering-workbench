#wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/RizkallaPhiReactive/RizkallaPhiReactive.sh; chmod 700 RizkallaPhi.sh;
concentrationNa2SO4=0.0004375
concentrationBaCl2=0.000350
TurbulentSchmidtNumber=0.75
Temperature=22
SimulationTime=10
RateConstant=__k__

macroFileName="RizkallaPhiReactive.java"

rm *.java
rm *.slurm.*
wget -q https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/RizkallaPhiReactive/$macroFileName
wget -q https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/RizkallaPhiReactive/rizkallaPhiReactive.slurm.unix3
wget -q https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/RizkallaPhiReactive/rizkallaPhiReactive.slurm.xeon40
wget -q https://github.com/jrbentzon/starccm-scale-thermodynamics/releases/download/v0.3.3/libuser.so

sed "s/__concentrationNa2SO4__/$concentrationNa2SO4/" -i $macroFileName
sed "s/__concentrationBaCl2__/$concentrationBaCl2/" -i $macroFileName
sed "s/__TurbulentSchmidtNumber__/$TurbulentSchmidtNumber/" -i $macroFileName
sed "s/__Temperature__/$Temperature/" -i $macroFileName
sed "s/__SimulationTime__/$SimulationTime/" -i $macroFileName
sed "s/__RateConstant__/$RateConstant/" -i $macroFileName
