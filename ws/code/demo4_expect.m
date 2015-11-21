function [res] = demo4(n)
	tic;
	val = sqrt(0.5 + foo(1:n, mod(1:n,2)));
	res = mean(val);
	toc;
end

function [res] = foo(x, sign)
	v = sqrt(x);
	res = sign .* (v) + (~sign) .* (1+v);
end

% demo4.m VS demo_expect.m (n = 50000)
% about 109 times slowdown, vector form is much faster

% demo4.m:
	% Elapsed time is 0.680603 seconds.

% demo_expect.m
	% Elapsed time is 0.006271 seconds.

