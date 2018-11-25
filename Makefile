all: compile

compile:
	antlr4 -no-listener -visitor Xpln.g4
	javac Xpln*.java Explain.java

clean:
	rm -f *.class

distclean: clean
	rm -f Xpln*.java *.class *.interp *.tokens
