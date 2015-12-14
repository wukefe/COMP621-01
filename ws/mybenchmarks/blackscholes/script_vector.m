function [tot] = script_vector(op)
N = 10;
tot = zeros(1, N);
for i = 1:N
    tot(i) = run_script_vector(op);
end
end