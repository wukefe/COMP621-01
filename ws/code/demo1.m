n = input('input n: ');
x = zeros(1,10);
t = y .* 2 ;
t = y ./ 2 ;
t = y  / 2 ;
for i = 1:n
	f(x) = 0;
	if i < 5
		x(i) = i * i;
	else
		x(i) = i + 2;
	end
	t(i) = x(i);
end