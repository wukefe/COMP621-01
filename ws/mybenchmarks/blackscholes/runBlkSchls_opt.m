function [elapsedTime] = runBlkSchls_opt(numOptions,otype,sptprice,strike,rate,volatility,otime,DGrefval)
numError = 0;
ERR_CHK  = 0;
tic;
prices = BlkSchlsOp(sptprice, strike, rate, volatility, otime, otype);
if ERR_CHK == 1
    numError = sum((DGrefval - prices) >= 1e-5);
end
elapsedTime = toc;
end

function [OptionPrice] = BlkSchlsOp(sptprice,strike,rate,volatility,time,otype)
xSqrtTime = sqrt(time);
logValues = log(sptprice ./ strike); xLogTerm  = logValues;
xRiskFreeRate = rate;
xDen  = volatility .* xSqrtTime;
xPowerTerm = volatility .* volatility .* 0.5;
xD1 = (xLogTerm + (xRiskFreeRate + xPowerTerm) .* time) ./ xDen; d1 = xD1;
xD2 = xD1 - xDen; d2 = xD2;
NofXd1 = CNDFOp( d1 );
NofXd2 = CNDFOp( d2 );
FutureValueX = strike .* ( exp( -(rate .* time) ) );
OptionPrice = otype .* ((FutureValueX .* (1 - NofXd2)) - (sptprice .* (1 - NofXd1)));
OptionPrice = OptionPrice + ((~otype) .* ((sptprice .* NofXd1) - (FutureValueX .* NofXd2)));
end

function OutputX = CNDFOp(InputX)
inv_sqrt_2xPI = 0.39894228040143270286;

sign = InputX < 0;
xInput = abs(InputX);
expValues = exp(-0.5 .* InputX .* InputX);
xNPrimeofX = inv_sqrt_2xPI .* expValues;

xK2   = 1 ./ (1 + 0.2316419 .* xInput);
xK2_2 = xK2 .* xK2;
xK2_3 = xK2 .* xK2_2;
xK2_4 = xK2 .* xK2_3;
xK2_5 = xK2 .* xK2_4;

xLocal_1 = xK2 .* 0.319381530;
xLocal_2 = (xK2_2 .* -0.356563782) + (xK2_3 .* 1.781477937) + (xK2_4 .* -1.821255978) + xK2_5 .* 1.330274429;
xLocal   = 1 - xNPrimeofX .* (xLocal_2 + xLocal_1);
OutputX  = (sign .* (1.0 - xLocal)) + (~sign) .* xLocal;

end