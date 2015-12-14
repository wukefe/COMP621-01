function [time] = run_micro1_new(N)
	t = 10;
	time = zeros(1, t);
	micro1_new(N); % discard first run
	for i = 1:t
		time(i) = micro1_new(N);
	end
end