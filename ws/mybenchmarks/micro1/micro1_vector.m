function [elapsetime] = micro1_new(n)
  tic;
  val = sqrt((0.5 + foo((1 : n), mod((1 : n), 2))));
%disp(val);
  res = mean(val);
  elapsetime = toc;
end
function [res] = foo(x, sign)
  ifcond0 = (sign == 1);
  elsecond = (~ifcond0);
  res = (((1 + sqrt(x)) .* elsecond) + (0.5 .* ifcond0));
%disp(res);
end

