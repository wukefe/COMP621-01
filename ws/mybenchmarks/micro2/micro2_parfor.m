function [elapsetime] = micro2_new(n)
  tic;
  val = zeros(1, n);
  parfor i = (1 : n)
    val(i) = (sin((sqrt(i) + 0.5)) * cos((sqrt(i) - 0.5)));
  end
%disp(val);
  res = mean(val);
  elapsetime = toc;
end

