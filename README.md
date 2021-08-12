# Ships Application

Using the gradle wrapper

## howto

### create the Artifacts:
```
cd ships
./gradlew build
```

### publish the Artifacts:
```
cd ships
./gradlew publishToMavenLocal
```

### start the springboot application:
(this needs a database)
```
cd ships
./gradlew bootRun
```

### create the docker image:
```
cd ships
./gradlew jibDockerBuild
```

