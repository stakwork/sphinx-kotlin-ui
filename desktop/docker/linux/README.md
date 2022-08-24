# Docker sphinx-kotlin-ui-packager-linux image

This is all the steps related to building the sphinx-kotlin-ui desktop linux app in docker.

## Building the sphinx-kotlin-ui-packager-linux docker packager

To build this image we need to run this command:

```shell
docker build -t sphinx-kotlin-ui-packager-linux:latest .
```

## Running the sphinx-kotlin-ui-packager-linux docker image

Need to run the following command in the `sphinx-kotlin-ui` directory. Also you need to close your IDE. 

```shell
docker run -it --mount type=bind,source="$(pwd)",target=/sphinx-kotlin-ui sphinx-kotlin-ui-packager-linux:latest 
```

Successfully running the script will have the following output.

```log
> Task :desktop:packageDeb
The distribution is written to /sphinx-kotlin-ui/desktop/build/compose/binaries/main/deb/sphinx_1.0.6-1_amd64.deb

BUILD SUCCESSFUL in 4m 36s
13 actionable tasks: 13 execute
```

### Local Properties

Due to the fact that the command to run the build "mounts" the `sphinx-kotlin-ui` directory all the details in the local.properties file will be accessible to the gradle execution. This basicallly means that if binary signing is introduce for the linux build all it would require is for the `build.gradle.kts` to be updated and for the required signing files (certs) to be mounted when the docker image is ran and also for the filepaths to these required files to be added to the `local.properties`.
