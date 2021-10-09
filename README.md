## Orders list app

## Requirements for build and launch
- npm
- java
- clojure
- datomic dev

### [Already built jar app](https://drive.google.com/file/d/1iup08ek75fupHLPb4y6ugHkjP_T2lWyJ/view?usp=sharing)

### Installation steps
#### Install datomic-dev (my version is "0.9.235")
- https://docs.datomic.com/cloud/dev-local.html
(probably you have to update deps with your version)

#### Build jar
```
make jar
```

#### Launch app
```
make run
```

#### App avaliable on http://localhost:9999

### Missing parts
####
- Error handling
- Data validation
- UI tests
- Configuration from envs

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
