.PHONY: install test release ui dev build jar run

install:
	npm install

release:
	rm -rf ./resources/public/js
	npx shadow-cljs release app

ui:
	npx shadow-cljs watch app

dev:
	clojure -A:test:repl

test:
	clojure -A:test

build: 
	rm -rf ./classes
	mkdir ./classes
	clojure -e "(compile 'orders.core)"
	clojure -A:build --main-class orders.core --target ./target/testapp.jar

jar: install release build

run:
	java -jar ./target/testapp.jar
