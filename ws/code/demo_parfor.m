function demo_parfor()
n = 900000;
A = zeros(1, n);
B = zeros(1, n);
tic;
parfor i = 1:n
  A(i) = foo(B(i) + i);
end
toc;
disp(A(1:5));
end

function [res] = foo(x)
res = sqrt(x);
end