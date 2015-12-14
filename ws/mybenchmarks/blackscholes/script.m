function script(op)
% 0: original
% 1: vector
% 2: parfor
% 3: optimized by hand
if (op == 0)
    tot1 = script_new(1);
    tot2 = script_new(2);
    tot3 = script_new(3);
    tot4 = script_new(4);
elseif (op == 1)
    tot1 = script_vector(1);
    tot2 = script_vector(2);
    tot3 = script_vector(3);
    tot4 = script_vector(4);
elseif (op == 2)
    tot1 = script_parfor(1);
    tot2 = script_parfor(2);
    tot3 = script_parfor(3);
    tot4 = script_parfor(4);
else
	tot1 = script_opt(1);
    tot2 = script_opt(2);
    tot3 = script_opt(3);
    tot4 = script_opt(4);
end
fprintf('%f&%f&%f&%f\n',min(tot1),max(tot1),mean(tot1),std(tot1));
fprintf('%f&%f&%f&%f\n',min(tot2),max(tot2),mean(tot2),std(tot2));
fprintf('%f&%f&%f&%f\n',min(tot3),max(tot3),mean(tot3),std(tot3));
fprintf('%f&%f&%f&%f\n',min(tot4),max(tot4),mean(tot4),std(tot4));
end