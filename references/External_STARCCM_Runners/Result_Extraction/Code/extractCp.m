% See post.m for a general description

close all; clearvars; clc

macroFile = 'addCpSecWingClean.java';

% Wing planform geometry for x-coordinates normalization
Croot = 2.57;
Ckink = 2.57;
Ctip = 1.41;
Yroot = 0;
Ykink = 4.75;
Ytip = 12.286;

% Section spanwise coordinates (m)
yCp = [0.5, 2.27, 3.81, 4.35, 5.89, 6.43, 7.97, 8.51, 10.05, 11.5];
nsec_inp = length(yCp);

% Calculation of the chord length
chord = zeros(1,nsec_inp);
for i = 1:nsec_inp
    if yCp(i) <= Ykink
        chord(i) = Croot;
    else
        chord(i) = Ckink + (Ctip - Ckink) / (Ytip - Ykink) * (yCp(i) - Ykink);
    end
end

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

disp('Extracting Cp distributions...')
for i = 1:length(array)
    fileName = [filePath,prefix,num2str(array(i),formatString)];
    star = ['starccm+.exe "', fileName, '.sim" -batch "', macroFile, '" -power -podkey ', pod, ' -licpath 1999@flex.cd-adapco.com'];
    dos(star);
end

% Move csv file into source folder if this script is called from another folder
if ~strcmp(filePath,[pwd,'\'])
    movefile('*_Cp.csv',filePath);
end

%% Organize results

% Cycle over the selected sim files to get the desired variables
spreadsheetFile = ['Cp_',prefix,'.xls'];
for i = 1:length(array)
    csvName = [filePath,prefix,num2str(array(i),formatString),'_Cp.csv'];
    cpArray_meters = readmatrix(csvName);

    % Calculate non-dimensional Cp coordinates
    cpArray_unit = cpArray_meters;

    % Need a check on the number of sections (must be equal to input yCp)
    % otherwise the non-dimensional Cp coordinates will not be updated and
    % throws a warning
    nsec_csv = size(cpArray_meters,2) / 2;
    if nsec_csv == nsec_inp
        cpArray_unit(:,1:2:end) = cpArray_meters(:,1:2:end) ./ ...
            repmat(chord,size(cpArray_unit,1),1);
    else
        warning(['WARNING: input number of sections mismatch with the ',...
            'number of sections read from file. Normalization will not be ',...
            'performed and the Cp array will be duplicated in the final table'])
    end

    % Matrix including both dimensional and non-dimensional Cp coordinates
    A = [ cpArray_meters; ...
        nan(size(cpArray_unit,2)); ...
        nan(size(cpArray_unit,2)); ...
        cpArray_unit];

    % Dynamically assign variables name
    a1 = arrayfun(@(y) sprintf('x%d', y), 1:size(A,2)/2, 'UniformOutput', false);
    a2 = arrayfun(@(y) sprintf('cp%d', y), 1:size(A,2)/2, 'UniformOutput', false);
    aa = [a1; a2];
    varNames = aa(:)'; % alternate 'x' and 'cp' variable name

    T = array2table(A,'VariableNames',varNames);

    disp(['Writing Cp table for alpha ', num2str(array(i)), ' on spreadsheet...'])
    writetable(T,spreadsheetFile,'Sheet',['AoA ', num2str(array(i))])
end

% Draw charts in the excel file
startRow = size(A,1) - size(cpArray_unit,1) + 2;    % read from this row
endRow = size(A,1) + 1; % last row to be read

for i = 1:length(array)
    disp(['Generating excel chart for alpha ', num2str(array(i)), '...'])
    drawExcelChart(spreadsheetFile, ['AoA ', num2str(array(i))], ...
        startRow, endRow, yCp);
end

if ~strcmp(filePath,[pwd,'\'])
    movefile(spreadsheetFile,filePath);
end

disp('END')