function [res] = demo4()
	tic;
	n = 50000;
	val = zeros(1, n);
	for i = 1:n
		val(i) = sqrt(0.5 + foo(i, mod(i,2)));
	end
	%disp(val);
	res = mean(val);
	toc;
end

function [res] = foo(x, sign)
	v = sqrt(x);
	if (sign == 1)
		res = v;
	else
		res = 1 + v;
	end
	%disp(res);
end