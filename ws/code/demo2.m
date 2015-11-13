function run(n)
	x0 = n >= 1;
	x1 = ~ (n > 1);
	x2 = n && 1;
	x3 = n || 1;
	for i = 1:n
		disp(i);
	end
end

function run2()
	n = 10;
	for i = 1:n
		disp(i);
	end
end