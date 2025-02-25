# Makefile

install:
	./gradle install

run-dist:
	# Очистка от результатов предыдущей сборки
	./gradle clean
	# Создание jar исполняемого файла
	./gradle installDist

test:
	./gradle test

report:
	./gradle jacocoTestReport
