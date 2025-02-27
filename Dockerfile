FROM gradle:8.5-jdk21

COPY . .

RUN gradle installDist

CMD ./build/install/app/bin/app