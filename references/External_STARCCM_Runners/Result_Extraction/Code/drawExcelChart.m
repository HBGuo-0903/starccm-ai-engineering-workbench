function drawExcelChart(spreadsheetFile,sheetName,startRow,endRow,station)

% Start Excel and open workbook
Excel = actxserver('Excel.Application');
WB = Excel.Workbooks.Open([pwd, '\', spreadsheetFile]);
% Show the workbook
Excel.visible = 0;
% Add chart
Chart = invoke(WB.Charts,'Add');
% Get Sheet object
SheetObj = Excel.Worksheets.get('Item', sheetName);
Chart.ChartArea.ClearContents;
% Name chart sheet
Chart.Name = ['Chart ', sheetName];

% Set source data range of chart
% X and Y data can also be set to Matlab arrays, by Srs.Values and Srs.XValues, ensuring equal length
for i = 1:length(station)
    % Y data starting at B column, then move to + 2 letters each step
    Srs = Chart.SeriesCollection.Add(SheetObj.Range([char(66+2*(i-1)),num2str(startRow),':',char(66+2*(i-1)),num2str(endRow)]));
    % X data starting at A column, then move to + 2 letters each step
    Srs.XValues = SheetObj.Range([char(65+2*(i-1)),num2str(startRow),':',char(65+2*(i-1)),num2str(endRow)]);
    % Series name
    Srs.Name = ['y ', num2str(station(i))];
end

% For chart types,  see https://msdn.microsoft.com/en-us/library/office/ff837417.aspx
Chart.ChartType = 'xlXYScatter';
% Set chart title,  see https://msdn.microsoft.com/en-us/library/office/ff196832.aspx
% Chart.HasTitle = true;
% Chart.ChartTitle.Text = 'Test Title';
% % Set chart legend, see https://msdn.microsoft.com/en-us/library/office/ff821884.aspx
% Chart.HasLegend = true;
% Customization
Chart.ApplyLayout(10);
Chart.Axes(2).ReversePlotOrder = 1;
Chart.Axes(1).AxisTitle.Text = 'x/c';
Chart.Axes(2).AxisTitle.Text = 'Cp';

WB.Save;
WB.Close;
end