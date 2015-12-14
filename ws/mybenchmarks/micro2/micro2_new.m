function [elapsetime] = micro2_new(n)
	tic;
	val = zeros(1, n);
	for i = 1:n
		x = sqrt(i) + 0.5;
		y = sqrt(i) - 0.5;
		val(i) = sin(x) * cos(y);
	end
	%disp(val);
	res = mean(val);
	elapsetime = toc;
end