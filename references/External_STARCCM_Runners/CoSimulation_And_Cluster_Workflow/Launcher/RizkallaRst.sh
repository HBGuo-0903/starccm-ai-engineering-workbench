#wget https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/RizkallaRst/RizkallaRst.sh; chmod 700 RizkallaRst.sh;
RPM=__iRPM__
STEPS=10000
macroFileName="RizkallaRst.java"

rm *.java
rm *.slurm.*
wget -q https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/RizkallaRst/$macroFileName
wget -q https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/RizkallaRst/rizkallaRst.slurm.unix3
wget -q https://raw.githubusercontent.com/jrbentzon/starccm-scale-thermodynamics/main/src/RizkallaRst/rizkallaRst.slurm.xeon40

sed "s/__RPM__/$RPM/" -i $macroFileName
sed "s/__STEPS__/$STEPS/" -i $macroFileName
