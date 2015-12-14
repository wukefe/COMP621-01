function [elapsetime] = micro2_opt(n)
	tic;
	x = sqrt(1:n);
	val = sin(x + 0.5) .* cos(x - 0.5);
	%disp(val);
	res = mean(val);
	elapsetime = toc;
end