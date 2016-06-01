all: DepthStreamCallback.c
	g++ -fPIC -c -Wall -I"/usr/lib/jvm/jdk1.8.0_77/include" -I"/usr/lib/jvm/jdk1.8.0_77/include/linux" DepthStreamCallback.c
	g++ -shared -o libfreenectDepth.so DepthStreamCallback.o
clean: 
	$(RM) DepthStreamCallback.o libfreenectDepth.so
