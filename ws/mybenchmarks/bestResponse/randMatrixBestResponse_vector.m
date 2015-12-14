function [A1, A2, P1, P2] = randMatrixBestResponse_new(numMoves, numRounds)
%function [A1,A2,P1,P2] = Benchmark(numMoves,numRounds)
  [A1, A2, P1, P2] = bestResponse(rand(numMoves, numMoves), rand(numMoves, numMoves), randi(numMoves), randi(numMoves), numRounds, 0.6);
end
function [A1, A2, P1, P2] = bestResponse(M1, M2, a1, a2, N, p)
  rowSize = size(M1, 1);
  columnSize = size(M1, 2);
  P = zeros(rowSize, 1);
  U1 = zeros(rowSize, 1);
  Q = zeros(1, columnSize);
  U2 = zeros(1, columnSize);
  A1 = zeros(N, 1);
  A2 = zeros(N, 1);
  for i = (1 : N)
    P(a1) = (P(a1) + 1);
    Q(a2) = (Q(a2) + 1);
    A1(i) = a1;
    A2(i) = a2;
%Given Q, what is the best response for row player?
%need to calculate the utility function for
%each possible move...
%U1 is a vector that holds the score for each move
    for j = (1 : rowSize)
%for each of row players moves
      U1(j) = (M1(j, :) * ((Q / i)'));
    end
    for j = (1 : columnSize)
%for each of column players moves
      U2(j) = ((M2(:, j)') * (P / i));
    end
%iterate through U1 to get the max index
    max = 0;
    bestMoves = zeros(1, rowSize);
    numMoves = 0;
    index = 0;
    for j = (1 : rowSize)
      ifcond0 = (U1(j) > max);
      ifcond1 = ((~ifcond0) & (U1(j) == max));
      elsecond = (~(ifcond0 | ifcond1));
      a1 = (((a1 .* ifcond1) + (j .* ifcond0)) + (a1 .* elsecond));
      max = (((max .* ifcond1) + (U1(j) .* ifcond0)) + (max .* elsecond));
      numMoves = ((((numMoves + 1) .* ifcond1) + (1 .* ifcond0)) + (numMoves .* elsecond));
      bestMoves(1) = (((bestMoves(1) .* ifcond1) + (a1 .* ifcond0)) + (bestMoves(1) .* elsecond));
      bestMoves(numMoves) = (((j .* ifcond1) + (bestMoves(numMoves) .* ifcond0)) + (bestMoves(numMoves) .* elsecond));
    end
% yy = 1;
%Only take this path on 1-p occassions
    ifcond0 = ((numMoves > 1) && (rand > p));
    elsecond = (~ifcond0);
    a1 = ((a1 .* elsecond) + (bestMoves(randi(numMoves)) .* ifcond0));
% yy = 2;
    bestMoves = zeros(1, columnSize);
    numMoves = 0;
    max = 0;
    for j = (1 : columnSize)
      ifcond0 = (U2(j) > max);
      ifcond1 = ((~ifcond0) & (U2(j) == max));
      elsecond = (~(ifcond0 | ifcond1));
      a2 = (((a2 .* elsecond) + (j .* ifcond0)) + (a2 .* ifcond1));
      max = (((max .* elsecond) + (U2(j) .* ifcond0)) + (max .* ifcond1));
      numMoves = (((numMoves .* elsecond) + (1 .* ifcond0)) + ((numMoves + 1) .* ifcond1));
      bestMoves(1) = (((bestMoves(1) .* elsecond) + (a2 .* ifcond0)) + (bestMoves(1) .* ifcond1));
      bestMoves(numMoves) = (((bestMoves(numMoves) .* elsecond) + (bestMoves(numMoves) .* ifcond0)) + (j .* ifcond1));
    end
%Only take this path on 1-p occassions
    ifcond0 = ((numMoves > 1) && (rand > p));
    elsecond = (~ifcond0);
    a2 = ((bestMoves(randi(numMoves)) .* ifcond0) + (a2 .* elsecond));
  end
  P1 = (P / N);
  P2 = (Q / N);
end

