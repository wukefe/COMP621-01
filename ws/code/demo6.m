function [r] = demo6(n)
d1 = n + 1;
if d1 > 1
	d1 = sqrt(n);
	r = d1 + 1;
else
	r =  - n;
end

end
