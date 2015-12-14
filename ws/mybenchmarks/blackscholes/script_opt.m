function [tot] = script_opt(op)
N = 10;
tot = zeros(1, N);
for i = 1:N
    tot(i) = run_script_opt(op);
end
end