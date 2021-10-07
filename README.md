## Orders list app

## Requirements for build and launch
- npm
- java
- clojure
- datomic dev


### Installation steps
#### Install datomic-dev (v "0.9.235")
- https://docs.datomic.com/cloud/dev-local.html


#### Build jar
```
make jar
```

#### Launch app
```
make run
```

#### App avaliable on http://localhost:9999


### Useful commands and local development

#### UI
##### Install UI deps
```
make install
```

##### UI dev repl
```
make ui
```

##### Release UI build
```
make release
```

##### Release UI build
```
make release
```

#### Server
##### Backend repl
```
make dev 
```

##### Run tests
```
make test
```

##### Complile and build jar
```
make build
```
