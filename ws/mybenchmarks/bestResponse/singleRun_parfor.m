function singleRun_parfor(x, y)
	rng(111)
	%[A1,A2,P1,P2] = randMatrixBestResponse(1000,500);
	% [A1,A2,P1,P2] = randMatrixBestResponse_new(1000,500);

	N = 10;
	% x = 100;
	% y = 50;
	[A1,A2,P1,P2] = randMatrixBestResponse_parfor(x,y);
	time = zeros(1, N);
	for i = 1:N
	tic;
	[A1,A2,P1,P2] = randMatrixBestResponse_parfor(x,y);
	time(i) = toc;
	end

	% disp(mean(time));
	fprintf('%f&%f&%f&%f\n',min(time),max(time),mean(time),std(time));
end