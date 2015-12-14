function [elapsedTime] = runBlkSchls_new(numOptions, otype, sptprice, strike, rate, volatility, otime, DGrefval)
  numError = 0;
  writeOut = 0;
% pass shape information
% otype      = reshape(otype      ,1,numOptions);
% sptprice   = reshape(sptprice   ,1,numOptions);
% strike     = reshape(strike     ,1,numOptions);
% rate       = reshape(rate       ,1,numOptions);
% volatility = reshape(volatility ,1,numOptions);
% otime      = reshape(otime      ,1,numOptions);
% DGrefval   = reshape(DGrefval   ,1,numOptions);
  prices = zeros(1, numOptions);
  tic;
  parfor i = (1 : numOptions)
prices(i) = BlkSchls(sptprice(i), strike(i), rate(i), volatility(i), otime(i), otype(i), 0);
  end
  for i = (1 : numOptions)
    ifcond0 = (0 == 1);
    elsecond = (~ifcond0);
    if (0 == 1)
      ifcond0 = ((DGrefval(i) - prices(i)) >= 1e-5);
      elsecond = (~ifcond0);
      numError = ((numError .* elsecond) + ((numError + 1) .* ifcond0));
      if ((DGrefval(i) - prices(i)) >= 1e-5)
% fprintf('error at %d\n',i);
      end
    end
  end
  elapsedTime = toc;
% if writeOut == 1
% fprintf('elapsed time is %f s\n',elapsedTime);
% WritePrice(prices, strcat('output_runBlkSchls_',char(fileNames(opt))));
% end
% if ERR_CHK == 1
% fprintf('total error number is %d\n',numError);
% end
end
function [OptionPrice] = BlkSchls(sptprice, strike, rate, volatility, time, otype, timet)
  xStockPrice = sptprice;
  xStrikePrice = strike;
  xVolatility = volatility;
  xTime = time;
  xDen = (xVolatility .* sqrt(xTime));
  xD1 = ((((rate + ((xVolatility .* xVolatility) .* 0.5)) .* xTime) + log((sptprice ./ strike))) ./ xDen);
  NofXd1 = CNDF(xD1);
  NofXd2 = CNDF((xD1 - xDen));
  FutureValueX = (strike .* exp(((-rate) .* time)));
  ifcond0 = (otype == 0);
  elsecond = (~ifcond0);
  OptionPrice = ((((sptprice .* NofXd1) - (FutureValueX .* NofXd2)) .* ifcond0) + (((FutureValueX .* (1.0 - NofXd2)) - (sptprice .* (1.0 - NofXd1))) .* elsecond));
end
function [OutputX] = CNDF(InputX)
  ifcond0 = (InputX < 0);
  elsecond = (~ifcond0);
  sign = ((1 .* ifcond0) + (0 .* elsecond));
  InputX = (((-InputX) .* ifcond0) + (InputX .* elsecond));
  xK2 = (1 ./ (1 + (0.2316419 .* InputX)));
  xK2_2 = (xK2 .* xK2);
  xK2_3 = (xK2_2 .* xK2);
  xK2_4 = (xK2_3 .* xK2);
  xLocal_2 = (xK2_2 .* (-0.356563782));
  xLocal_3 = (xK2_3 .* 1.781477937);
  xLocal_2 = (xLocal_2 + xLocal_3);
  xLocal_3 = (xK2_4 .* (-1.821255978));
  xLocal_2 = (xLocal_2 + xLocal_3);
  xLocal_3 = ((xK2_4 .* xK2) .* 1.330274429);
  xLocal_2 = (xLocal_2 + xLocal_3);
  OutputX = (1.0 - ((xLocal_2 + (xK2 .* 0.319381530)) .* (exp((((-0.5) .* InputX) .* InputX)) .* 0.39894228040143270286)));
  ifcond0 = sign;
  elsecond = (~ifcond0);
  OutputX = (((1.0 - OutputX) .* ifcond0) + (OutputX .* elsecond));
end

