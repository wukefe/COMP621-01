
targetfolder=hanfeng

# bestResponse
src0=benchmarks/bestResponse
tar0=$(src0)/$(targetfolder)/
all0=randMatrixBestResponse.m bestResponse.m

# blackscholes
src1=benchmarks/blackscholes
tar1=$(src1)/$(targetfolder)/
all1=runBlkSchls.m BlkSchls.m CNDF.m

# CLLL
src2=benchmarks/CLLL
tar2=$(src2)/$(targetfolder)/
all2=CLLL.m size_reduce_k.m

# gaborfilter
src3=benchmarks/gaborfilter
tar3=$(src3)/$(targetfolder)/
all3=runFilter.m benchmarking.m gaborFeatures.m gaborFilterBank.m imageReader.m

# keypointsdetectionprogram
src4=benchmarks/keypointsdetectionprogram
tar4=$(src4)/$(targetfolder)/
all4=keypointsdetectionprogram.m

# kmeans
src5=benchmarks/kmeans
tar5=$(src5)/$(targetfolder)/
all5=litekmeans.m

# md
src6=benchmarks/md
tar6=$(src6)/$(targetfolder)/
all6=md.m

# MNISTBenchmark
src7=benchmarks/MNISTBenchmark
tar7=$(src7)/$(targetfolder)/
all7=mnistclassify.m backpropclassify.m CG_CLASSIFY.m CG_CLASSIFY_INIT.m CG_MNIST.m makebatches.m minimize.m rbm.m

# nbodyVect
src8=benchmarks/nbodyVect
tar8=$(src8)/$(targetfolder)/
all8=driver_simulation_nbodyVect.m simulation_nbodyVect.m nbodyVect.m

# rch
src9=benchmarks/rch
tar9=$(src9)/$(targetfolder)/
all9=rch.m

# tsp_dp
src10=benchmarks/tsp_dp
tar10=$(src10)/$(targetfolder)/
all10=tsp_dp1.m

# Create benchmarks
func: processInput.java
	javac processInput.java
	java -cp . processInput $(all0) -I$(src0)/src/ -O$(tar0)
	java -cp . processInput $(all1) -I$(src1)/src/ -O$(tar1)
	java -cp . processInput $(all2) -I$(src2)/src/ -O$(tar2)
	java -cp . processInput $(all3) -I$(src3)/src/ -O$(tar3)
	java -cp . processInput $(all4) -I$(src4)/src/ -O$(tar4)
	java -cp . processInput $(all5) -I$(src5)/src/ -O$(tar5)
	java -cp . processInput $(all6) -I$(src6)/src/ -O$(tar6)
	java -cp . processInput $(all7) -I$(src7)/src/ -O$(tar7)
	java -cp . processInput $(all8) -I$(src8)/src/ -O$(tar8)
	java -cp . processInput $(all9) -I$(src9)/src/ -O$(tar9)
	java -cp . processInput $(all10) -I$(src10)/src/ -O$(tar10)