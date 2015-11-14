function [res] = demo3()
	n = 10;
	x = zeros(1, n);
	for i = 1:n
		x(i) = sqrt(i);
	end
	res = sum(x);
end