JFLAGS = -g
JC = javac
JVM = java
FILE = 
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Matching.java \
	App.java

MAIN = App

default: classes

classes: $(CLASSES:.java=.class)

run: $(MAIN).class
	$(JVM) $(MAIN) $(FILE)
clean:
	$(RM) *.class

# Adapted from
# http://profesores.elo.utfsm.cl/~agv/elo329/Java/javamakefile.html
