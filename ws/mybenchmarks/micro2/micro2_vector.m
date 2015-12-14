function [elapsetime] = micro2_new(n)
  tic;
  val = (sin((sqrt((1 : n)) + 0.5)) .* cos((sqrt((1 : n)) - 0.5)));
%disp(val);
  res = mean(val);
  elapsetime = toc;
end

