function [time] = run_micro2_vector(num)
	N = 20;
	time = zeros(1, N);
	for i = 1:N
		time(i) = micro2_vector(num);
	end
end