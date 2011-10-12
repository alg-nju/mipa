function occurrence(expIndex, prob)

%CREATEFIGURE(expIndex,prob)
%  expIndex:  stem x
%  prob:  stem y

% for experiment 1, the origin data is as follows.
% they should be passed in the function as parameters.

% expIndex = [0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19];
% prob = [40 38 46 35 31 45 36 36 31 39 40 35 36 47 39 39 37 27 28 33];
% prob = prob / 100;

% Create figure
fig = figure('Color',[1 1 1]);
colormap('lines');

% Create axes
axesObj = axes('Parent',fig,'YGrid','on',...
    'XTick',[0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20],...
    'FontWeight','bold',...
    'FontSize',15,...
    'FontName','Times New Roman',...
    'FontAngle','italic');

box(axesObj,'on');
hold(axesObj,'all');

% Create xlabel
xlabel('n^{th} Experiment', 'Interpreter','tex', 'FontWeight','bold', 'FontSize',16,...
    'FontName','Times New Roman');

% Create ylabel
ylabel('Prob_{inc}','Interpreter','tex',...
    'FontWeight','bold',...
    'FontSize',16,...
    'FontName','Times New Roman');
ylim([0 0.55]);

% Create stem
stemObj = stem(expIndex,prob,'LineWidth',2,'Color',[0 0 1],'Parent',axesObj,...
    'DisplayName','actual values');

% Get xdata from plot
xdata = get(stemObj, 'xdata');
% Get ydata from plot
ydata = get(stemObj, 'ydata');
% Make sure data are column vectors
xdata = xdata(:);
ydata = ydata(:);

% Get axes xlim
axXLim = get(axesObj, 'xlim');

% Find the mean
ymean = mean(ydata);
% Get coordinates for the mean line
meanValue = [ymean ymean];
% Plot the mean
statLine = plot(axXLim,meanValue,'DisplayName','mean value (0.369)',...
    'Parent',axesObj,...
    'Tag','mean y',...
    'LineStyle','-.',...
    'Color',[0 0.5 0]);

% Set new line in proper position
setLineOrder(axesObj, statLine, stemObj);

% Create legend
legendObj = legend(axesObj,'show');
set(legendObj,'EdgeColor',[0 0.5 0],'Orientation','horizontal',...
    'Location','Best',...
    'YColor',[0 0.5 0],...
    'XColor',[0 0.5 0],...
    'FontSize',12);

%-------------------------------------------------------------------------%
function setLineOrder(axesh, newLine, associatedLine)
%SETLINEORDER(AXESH1,NEWLINE1,ASSOCIATEDLINE1)
%  Set line order
%  AXESH1:  axes
%  NEWLINE1:  new line
%  ASSOCIATEDLINE1:  associated line

% Get the axes children
hChildren = get(axesh,'Children');
% Remove the new line
hChildren(hChildren==newLine) = [];
% Get the index to the associatedLine
lineIndex = find(hChildren==associatedLine);
% Reorder lines so the new line appears with associated data
hNewChildren = [hChildren(1:lineIndex-1);newLine;hChildren(lineIndex:end)];
% Set the children:
set(axesh,'Children',hNewChildren);

