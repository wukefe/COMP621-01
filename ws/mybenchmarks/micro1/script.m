function script(op)
N0 = 100000; % 0
N1 = 200000; % 1
N2 = 400000; % 2
N3 = 800000; % 3

if (op == 0)
	tot1 = run_micro1_new(N0);
	tot2 = run_micro1_new(N1);
	tot3 = run_micro1_new(N2);
	tot4 = run_micro1_new(N3);
elseif (op == 1)
	tot1 = run_micro1_vector(N0);
	tot2 = run_micro1_vector(N1);
	tot3 = run_micro1_vector(N2);
	tot4 = run_micro1_vector(N3);
elseif (op == 2)
	tot1 = run_micro1_parfor(N0);
	tot2 = run_micro1_parfor(N1);
	tot3 = run_micro1_parfor(N2);
	tot4 = run_micro1_parfor(N3);
else
	tot1 = run_micro1_opt(N0);
	tot2 = run_micro1_opt(N1);
	tot3 = run_micro1_opt(N2);
	tot4 = run_micro1_opt(N3);
end
fprintf('%f&%f&%f&%f\n',min(tot1),max(tot1),mean(tot1),std(tot1));
fprintf('%f&%f&%f&%f\n',min(tot2),max(tot2),mean(tot2),std(tot2));
fprintf('%f&%f&%f&%f\n',min(tot3),max(tot3),mean(tot3),std(tot3));
fprintf('%f&%f&%f&%f\n',min(tot4),max(tot4),mean(tot4),std(tot4));
end

