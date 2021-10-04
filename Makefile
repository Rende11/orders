.PHONY: test

dev:
	clojure -A:test:repl

test:
	clojure -A:test
