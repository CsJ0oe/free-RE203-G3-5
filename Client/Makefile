all : compile

clean :
	rm -rf build/*

run: compile
	java -classpath build/ Client 127.0.0.1 8767 data/

compile :
	javac -d build/ -cp build/ src/*.java



