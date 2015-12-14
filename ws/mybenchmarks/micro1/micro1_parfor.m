function [elapsetime] = micro1_new(n)
  tic;
  val = zeros(1, n);
  parfor i = (1 : n)
    val(i) = sqrt((0.5 + foo(i, mod(i, 2))));
  end
%disp(val);
  res = mean(val);
  elapsetime = toc;
end
function [res] = foo(x, sign)
  ifcond0 = (sign == 1);
  elsecond = (~ifcond0);
  res = ((0.5 .* ifcond0) + ((1 + sqrt(x)) .* elsecond));
%disp(res);
end

