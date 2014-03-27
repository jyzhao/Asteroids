all: generate
	javac -d . src/*.java

clean:
	rm -rf classes build
	rm -rf bin
	rm *.class

.PHONY: all generate clean
