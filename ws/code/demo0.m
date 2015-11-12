x = zeros(1, n);
y = zeros(1, n);
for i=1:n
	y(i) = x(i) + 1 / (2 - w);
	s = i + 2;
end