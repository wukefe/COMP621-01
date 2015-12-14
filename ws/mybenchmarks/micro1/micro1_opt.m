function [elapsetime] = micro1_opt(n)
	tic;
	x = 1:n;
	sign = mod(x, 2);
	val = sqrt(0.5 + 0.5 .* sign + (1 + sqrt(x)) .* (~sign));
	res = mean(val);
	elapsetime = toc;
end