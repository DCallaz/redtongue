all: clean comp install

clean:
	rm -f src/*.class

comp:
	javac -d bin/ src/*.java

install:
	jar cmvf META-INF/MANIFEST.MF RedTongue.jar -C bin .
