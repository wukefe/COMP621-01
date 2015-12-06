function [A1,A2,P1,P2] = Benchmark(numMoves,numRounds)
    M1 = rand(numMoves,numMoves);
    M2 = rand(numMoves,numMoves);
    a1 = randi(numMoves);
    a2 = randi(numMoves);
    p = 0.6;
    [A1,A2,P1,P2] = bestResponse(M1,M2,a1,a2,numRounds,p);

end
function [A1,A2,P1,P2] = bestResponse(M1,M2,a1,a2,N,p)

rowSize = size(M1,1);
columnSize = size(M1,2);

P = zeros(rowSize,1);
U1 = zeros(rowSize,1);
Q = zeros(1,columnSize);
U2 = zeros(1,columnSize);

A1 = zeros(N,1);
A2 = zeros(N,1);

for i = 1:N
    P(a1) = P(a1)+ 1;
    Q(a2) = Q(a2)+ 1;
    A1(i) = a1;
    A2(i) = a2;
    %Given Q, what is the best response for row player?
    %need to calculate the utility function for
    %each possible move...
    %U1 is a vector that holds the score for each move
    for j = 1: rowSize
        %for each of row players moves
        U1(j) = M1(j,:)*(Q/i)';
    end
    for j = 1: columnSize
        %for each of column players moves
        U2(j) = M2(:,j)'*(P/i);
    end
    
    
    %iterate through U1 to get the max index
    max = 0;
    bestMoves = zeros(1,rowSize);
    numMoves = 0;
    index = 0;
    for j = 1 : rowSize
        if U1(j) > max
            max = U1(j);
            a1 = j;
            numMoves = 1;
            bestMoves(1)= a1;
        elseif U1(j) == max
            numMoves = numMoves + 1;
            bestMoves(numMoves) = j;
        end
    end
    
    %Only take this path on 1-p occassions
    if numMoves > 1 && rand > p 
        index = randi(numMoves);   
         a1 = bestMoves(index);
    end
    
    bestMoves = zeros(1,columnSize);
    numMoves = 0;
    max = 0;
    for j = 1 : columnSize
        if U2(j)> max
            max = U2(j);
            a2 = j;
            numMoves = 1;
            bestMoves(1) = a2;
        elseif U2(j) == max
            numMoves = numMoves + 1;
            bestMoves(numMoves) = j;
        end
    end
    %Only take this path on 1-p occassions
    if numMoves > 1 && rand > p
        index = randi(numMoves);
        a2 = bestMoves(index);
    end
    
end

P1 = P/N;
P2 = Q/N;

end
