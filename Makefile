all: clean comp

clean:
	rm -f src/*.class

comp:
	javac -d bin/ src/*.java
