% Calls STAR-CCM+ in batch with a macro to get and plot the desired reports
% versus an independent variable. Useful to list and plot data from a
% design sweep like an airfoil investigated at several angles of attack,
% where each sim file is an angle of attack. Works with Microsoft Windows.
% The STAR-CCM+ executable starccm+.exe must be in the Windows path. This
% script is configured to work with Power-on-Demand (PoD) license. The
% appropriate PoD string must be written by the user in the pod.txt file.
%
% It is assumed that the sim file name has the numeric independent variable
% in the last characters. MATLAB will try to get the 'prefix' of this file
% name, i.e. the root of the name without the independent variable value.
% For instance:
%
% mysimfile_alpha0.0.sim  -->  prefix = mysimfile_alpha
% or
% mysimfile_alpha0.sim  -->  prefix = mysimfile_alpha
%
% This script will work well with the last characters as floating point
% digits, even with zero decimal positions. The script will ask the user to
% check if the prefix has been correctly captured, otherwise it asks the
% user to write the prefix.
%
% Then the script will ask the user to choose the sim files from which 
% extract all the reports with the JAVA macro 'Report_to_csv.java'. The sim
% files are all those with the same prefix previously defined, in the
% folder initially selected, so that it should be easy to get the files of
% interest even if the folder is populated with many and different file
% types.
%
% Finally, the script ask the user which report to plot versus the
% independent variable, from a list of all extracted reports. Sparse
% selection (e.g. non-contiguous angles of attack) is also permitted.

close all; clearvars; clc

%% Initialization
% From now on, the script is interactive.

% Ask user which file consider
[fileName, filePath] = uigetfile('.sim',...
    'Select any sim file of interest so that MATLAB can guess the ''prefix''');

% Predict predix in filename. Assume last 4 chars are file extension (.sim)
% Expect last chars to be a floating point number
simName = fileName(1:end-4);
numChars = length(simName);
position = numChars;
indication = str2double(simName(position));
% cycle backward through file name
while ~isnan(indication) || strcmp(simName(position),'.')
    position = position - 1;
    indication = str2double(simName(position));
end
prefix = simName(1:position);

% Ask user if prefix is correct
answer = questdlg({ ['File name:     ', simName],...
    ['Prefix:     ', prefix],...
    'Is that correct?'},'Check name','Yes','No','Yes');

% If not, make the user write the correct prefix
if strcmp(answer,'No')
    prefix = inputdlg('Write the sim name without variable (prefix)',...
        'Write prefix',[1,60]);
    position = length(prefix);
end

% Ask user to write the independent variable name
indVar = inputdlg('Write the independent variable name (no spaces, no latex symbols)',...
        'X-axis label',[1,40],{'variable'});

% Get the desired sim files
listing = dir([filePath,prefix,'*.sim']);
[indx,~] = listdlg('ListString',{listing.name},...
    'PromptString','Select sim files','ListSize',[450,250]);

% Check variable digit of precision
var = simName(position+1:end);
k = strfind(var,'.');
if isempty(k) % integer as floating point
    formatString = ['%',num2str(length(var)+1),'.0f']; % add a digit for safety
else
    formatString = ['%',num2str(length(var(1:k-1))+1),'.',num2str(length(var(k+1:end))),'f'];
end

% Get variable array
array = zeros(length(indx),1);
c = 0;
for i = indx
    c = c + 1;
    array(c) = str2double(listing(i).name(position+1:end-4));
end
array = sort(array);

%% Call STAR-CCM+

% Read PoD license from external file
fileID = fopen('pod.txt');
pod = fscanf(fileID,'%s');
fclose(fileID);

disp('Report generation...')
for i = 1:length(array)
    fileName = [filePath,prefix,num2str(array(i),formatString)];
    star = ['starccm+.exe "', fileName, '.sim" -batch "Report_to_csv.java" -power -podkey ', pod, ' -licpath 1999@flex.cd-adapco.com'];
    dos(star);
end

% Move csv file into source folder if this script is called from another folder
if ~strcmp(filePath,[pwd,'\'])
    movefile('*_report.csv',filePath);
end

%% Visualize results

% Get variables name and number from the first file of the list
csvName = [filePath,prefix,num2str(array(1),formatString),'_report.csv'];
aero = readtable(csvName,'ReadRowNames',true);

% Ask user which variable get and plot
[indx,~] = listdlg('ListString',aero.Row,...
    'PromptString','Select variables to plot','ListSize',[250,250]);

% Initialize variables array
varNumber = length(indx);
varName = aero.Row;
var = zeros(length(array),varNumber);

% Cycle over the selected sim files to get the desired variables
for i = 1:length(array)
    csvName = [filePath,prefix,num2str(array(i),formatString),'_report.csv'];
    aero = readtable(csvName,'ReadRowNames',true);
    
    for j = 1:varNumber
        var(i,j) = aero{indx(j),1};
    end

end

% Create output table
tsize = [size(var,1),size(var,2)+1];
out = table('Size',tsize,...
    'VariableTypes',repmat({'double'},tsize(2),1),...
    'VariableNames',[indVar; aero.Row(indx)]);

% Fill and display output table
out{:,1} = array;
for i = 1:size(var,2)
    out{:,i+1} = var(:,i);
end
disp(out)

% Plot data: selected variables versus the independent variable
for i = 1:size(var,2)
    figure
    plot(array,var(:,i),'o-','LineWidth',2)
    grid on
    xlabel(indVar,'Interpreter','none')
    ylabel(aero.Row{indx(i)},'Interpreter','none')
end
drawnow

%% Batch aerodynamic loads calculation
disp('Aerodynamic loads extraction...')

for i = 1:length(array)
    
    % write angle of attack into file
    fileID = fopen('aoa','w');
    fprintf(fileID, '%.1f\n', array(i));
    fclose(fileID);
    
    fileName = [filePath,prefix,num2str(array(i),formatString)];
    star = ['starccm+.exe "', fileName, '.sim" -batch "WingLoadBatch.java" -power -podkey ', pod, ' -licpath 1999@flex.cd-adapco.com'];
    dos(star);
    
    aero = readmatrix([prefix,num2str(array(i),formatString),'_loads.csv']);
    ClDist(:,i) = aero(:,end); %#ok<*SAGROW>
    cClDist(:,i) = aero(:,end-1);
end
movefile('*.csv',filePath)

disp('Cl distribution vs y/(b/2)')
disp([aero(:,2),ClDist])

disp('cCl distribution vs y/(b/2)')
disp([aero(:,2),cClDist])

disp('END')