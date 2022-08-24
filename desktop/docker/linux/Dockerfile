FROM ubuntu:jammy-20220801

# Update binutils, g++, gcc
RUN apt-get update -y && \
    apt-get install build-essential software-properties-common wget -y

# Install Java
ENV JAVA_HOME=/usr/java/15
ENV PATH=$JAVA_HOME/bin:$PATH
RUN JAVA_URL=https://corretto.aws/downloads/latest/amazon-corretto-15-x64-linux-jdk.tar.gz && \
    JAVA_ARCHIVE=/tmp/jdk.tar.gz && \
    JAVA_BASE=/usr/java/ && \
    wget $JAVA_URL --output-document $JAVA_ARCHIVE && \
    mkdir -p $JAVA_BASE && \
    tar -xzf $JAVA_ARCHIVE --directory $JAVA_BASE && \
    find $JAVA_BASE -type d -maxdepth 1 -name "amazon-corretto-15*linux-x64" -exec mv {} $JAVA_HOME \; && \
    rm $JAVA_ARCHIVE
ENV LANG=en_US.UTF-8 LANGUAGE=en_US:en LC_ALL=en_US.UTF-8
ENV JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF

COPY build-sphinx.sh /build-sphinx.sh

RUN chmod a+x /build-sphinx.sh

CMD ["/build-sphinx.sh"]
