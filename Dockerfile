FROM gradle:8.5-jdk21

WORKDIR /app

COPY . .

RUN gradle wrapper

RUN gradle installDist

ENV SPRING_PROFILES_ACTIVE=production

CMD ./build/install/app/bin/app