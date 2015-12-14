
# generated by pygen.py
# python pygen.py pytemplate

import crawlib as lib
from time import time

def main():
	content = lib.readLines('in_1024K')
	cutLines(524288, content, '512K')
	cutLines(262144, content, '256K')
	cutLines(131072, content, '128K')
	print 'done'
	return
	
def cutLines(n, v, fn):
	out = [str(n)]
	for x in range(n):
		out.append(v[x+1])
	lib.writeLines(out,'in_'+fn)

if __name__ == "__main__":
	t0 = time()
	main()
	print('execution time is %f s' % (time()-t0))