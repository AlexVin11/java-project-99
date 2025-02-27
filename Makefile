# Makefile

install:
	./gradlew install

clean-build:
	# Очистка от результатов предыдущей сборки
	./gradlew clean
	# Создание jar исполняемого файла
	./gradlew build

test:
	./gradlew test

report:
	./gradlew jacocoTestReport
