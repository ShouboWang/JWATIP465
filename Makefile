JFLAG = -g
JC = javac
.SUFFIXES: .java .class


.java.class:
	$(JC)$(JFLAGS) $*.java

CLASSES = \
		  pipair.java

default: classes

classes: $(CLASSES:.java=.class)

all: default

clean: 
	$(RM) *.class
