for i = 1:n
	f(x) = 0;
	if i < 5
		x(i) = i * i;
	else
		x(i) = i + 2;
	end
	t(i) = x(i);
end