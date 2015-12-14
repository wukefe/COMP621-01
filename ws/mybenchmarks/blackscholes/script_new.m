function [tot] = script_new(op)
N = 10;
tot = zeros(1, N);
for i = 1:N
    tot(i) = run_script_new(op);
end
end