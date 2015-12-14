function [time] = run_micro2_new(num)
	N = 20;
	time = zeros(1, N);
	for i = 1:N
		time(i) = micro2_new(num);
	end
end