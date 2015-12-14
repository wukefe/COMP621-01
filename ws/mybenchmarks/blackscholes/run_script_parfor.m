function [elapsedTime] = run_script_parfor(opt)
% numOptions, read from file
if or(opt < 1, opt > 4)
    error('wrong opt... opt should be either 1,2,3,4');
end
fileNames  = {'128K.txt','256K.txt','512K.txt','1024K.txt'};
% fileName   = strcat('./data/in_',char(fileNames(opt)));
fileName   = ['./data/in_',char(fileNames(opt))];
fileID     = fopen(fileName,'r');
numOptions = fscanf(fileID,'%d',1);
input      = textscan(fileID,'%f %f %f %f %f %f %c %f %f');
fclose(fileID);
disp('reading file has been done.');
% alloc
otype      = reshape(input{7} == 'P',1,numOptions);
sptprice   = reshape(input{1}       ,1,numOptions);
strike     = reshape(input{2}       ,1,numOptions);
rate       = reshape(input{3}       ,1,numOptions);
volatility = reshape(input{5}       ,1,numOptions);
otime      = reshape(input{6}       ,1,numOptions);
DGrefval   = reshape(input{9}       ,1,numOptions);

elapsedTime = runBlkSchls_parfor(numOptions,otype,sptprice,strike,rate,volatility,otime,DGrefval);
disp(elapsedTime);
end