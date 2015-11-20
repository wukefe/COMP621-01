function [res] = demo3()
	n = 10;
	x = zeros(1, n);
	for i = 1:n
		x(i) = sqrt(i);
	end
	res = foo('xyz');
	new = res;
	res = sum(x);
end

function [res] = foo(x)
	w = 2;
	if x > 10
		w = 'abc';
	end
	res = w;
end