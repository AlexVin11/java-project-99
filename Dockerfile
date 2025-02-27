FROM gradle:8.5-jdk21

COPY . .

RUN gradle clean build

CMD ./build/install/app/bin/app