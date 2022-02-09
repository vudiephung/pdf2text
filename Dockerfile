FROM openjdk:8-jdk-alpine
RUN apk update && apk add tesseract-ocr=3.04.01-r1 --repository=http://dl-cdn.alpinelinux.org/alpine/v3.6/community && \
    export LD_LIBRARY_PATH=/usr/local/lib
ENV TESSDATA_PREFIX /usr/local/share
RUN mkdir ${TESSDATA_PREFIX}/tessdata
RUN wget -O ${TESSDATA_PREFIX}/tessdata/eng.traineddata https://github.com/tesseract-ocr/tessdata/raw/4.00/eng.traineddata
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]