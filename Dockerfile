FROM sbtscala/scala-sbt:eclipse-temurin-jammy-21.0.2_13_1.9.8_2.13.12

ENV ENV_TYPE=${ENV_TYPE}

# Open up the application port
EXPOSE 80

# Copy the built package into the container
COPY target/universal/*.tgz /mnt/package.tgz

# Extract the build package
USER root
RUN tar zxvf /mnt/package.tgz --strip-components=1 -C /mnt && \
    rm /mnt/package.tgz

USER nobody

WORKDIR /mnt

# Enter the shell script to be run after Consul initialisation.
# You can pass any additional arguments to the application shell script
# if you specify it as an array of strings.
# eg. CMD ["script","arg_a","arg_b",...]
CMD ["/mnt/bin/talktodb", "-Dspring.profiles.active=${ENV_TYPE}"]
