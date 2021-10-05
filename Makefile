.PHONY: test

dev:
	clojure -A:test:repl

test:
	clojure -A:test


jar:
	rm -rf ./classes
	mkdir ./classes
	clojure -M -e "(compile 'orders.core)"
	clojure -A:build --main-class orders.core --target ./target/testapp.jar

run:
	java -jar ./target/testapp.jar
