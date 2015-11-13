n = input('input n');
x = zeros(1, n);
y = zeros(1, n);
w = 0.3;
k = 1 + 2i;
for i=1:n
	y(i) = x(i) + 1 / (2 - w);
	s = i + 2;
end