function [A1,A2,P1,P2] = randMatrixBestResponse_opt(numMoves,numRounds)
    M1 = rand(numMoves,numMoves);
    M2 = rand(numMoves,numMoves);
    a1 = randi(numMoves);
    a2 = randi(numMoves);
    p = 0.6;
    [A1,A2,P1,P2] = bestResponse_opt(M1,M2,a1,a2,numRounds,p);
end
function [A1,A2,P1,P2] = bestResponse_opt(M1,M2,a1,a2,N,p)

rowSize = size(M1,1);
columnSize = size(M1,2);

P = zeros(rowSize,1);
Q = zeros(1,columnSize);
 
A1 = zeros(N,1);
A2 = zeros(N,1);

for i = 1:N
    P( a1) = P(a1)+ 1;
    Q(a2) = Q(a2)+ 1;
    A1(i) = a1;
    A2(i) = a2;
    %Given Q, what is the best response for row player?
    %need to calculate the utility function for
    %each possible move...
    %U1 is a vector that holds the score for each move
    
    
    U1 = sum(bsxfun(@times,M1,Q/i),2);
    U2 = sum(bsxfun(@times,M2,P/i));
    
    
    bestMoves = find(U1==max(U1));
    numMoves = size(bestMoves,2);
    %Only take this path on 1-p occassions
    if numMoves > 1 && rand > p 
        index = randi(numMoves);   
         a1 = bestMoves(index);
    else 
         a1 = bestMoves(1);
    end
    
       
    bestMoves = find(U2==max(U2));
    numMoves = size(bestMoves,2);
    %Only take this path on 1-p occassions
    if numMoves > 1 && rand > p 
        index = randi(numMoves);   
         a2 = bestMoves(index);
    else 
        a2 = bestMoves(1);
    end
    
end

P1 = P/N;
P2 = Q/N;

end