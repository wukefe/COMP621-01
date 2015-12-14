
JavaFiles=*.java 

bench-blackscholes-vector=mybenchmarks/blackscholes/runBlkSchls_new.m mybenchmarks/blackscholes/runBlkSchls_vector.m 0 0
bench-blackscholes-parfor=mybenchmarks/blackscholes/runBlkSchls_new.m mybenchmarks/blackscholes/runBlkSchls_parfor.m 1 0

bench-bestresponse-vector=mybenchmarks/bestResponse/randMatrixBestResponse_new.m mybenchmarks/bestResponse/randMatrixBestResponse_vector.m 0 1
bench-bestresponse-parfor=mybenchmarks/bestResponse/randMatrixBestResponse_new.m mybenchmarks/bestResponse/randMatrixBestResponse_parfor.m 1 1

bench-micro1-vector=mybenchmarks/micro1/micro1_new.m mybenchmarks/micro1/micro1_vector.m 0 2
bench-micro1-parfor=mybenchmarks/micro1/micro1_new.m mybenchmarks/micro1/micro1_parfor.m 1 2

bench-micro2-vector=mybenchmarks/micro2/micro2_new.m mybenchmarks/micro2/micro2_vector.m 0 3
bench-micro2-parfor=mybenchmarks/micro2/micro2_new.m mybenchmarks/micro2/micro2_parfor.m 1 3


all:
	javac -cp McLabCore.jar $(JavaFiles)
	java -cp McLabCore.jar:. autoVector $(bench-blackscholes-vector)
	java -cp McLabCore.jar:. autoVector $(bench-blackscholes-parfor)
	java -cp McLabCore.jar:. autoVector $(bench-bestresponse-vector)
	java -cp McLabCore.jar:. autoVector $(bench-bestresponse-parfor)
	java -cp McLabCore.jar:. autoVector $(bench-micro1-vector)
	java -cp McLabCore.jar:. autoVector $(bench-micro1-parfor)
	java -cp McLabCore.jar:. autoVector $(bench-micro2-vector)
	java -cp McLabCore.jar:. autoVector $(bench-micro2-parfor)
	
clean:
	rm -f *.class