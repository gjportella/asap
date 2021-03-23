### Projeto R para analise de dados Spot da Amazon.
#
# Clear environment: rm(list=ls())
# Clear console: CTRL+L
#
# Os arquivos contem as seguintes informacoes separadas por ponto-e-virgula.
#
# Timestamp (HH:mm:ss dd/MM/yyyy)
# Preço SPOT (USD/h)
# Lance
# Disponibilidade
# Utilidade
# Valor médio do lance fixo (USD/h)  
# Janela com base no lance fixo (ate 12 horas)
# Valor médio do lance variável de hora em hora (USD/h)
# Janela com base no lance variável de hora em hora (ate 12 horas)
#
###

# Define o caminho atual do projeto.
PROJECT_PATH <- "C:/Development/Workspaces/R/ieee-tcc"
setwd(PROJECT_PATH)

### PROCESSAMENTO - INICIO

# Lib para calculo do percentil e CI
require(jmuOutlier)

# Variaveis do script
instanceTypes <- c("c5n.2xlarge", "c5n.9xlarge", "g3s.xlarge",
	"i3.2xlarge", "i3.8xlarge", "m5.2xlarge", "m5.4xlarge",
	"p2.xlarge", "r4.2xlarge", "r5.2xlarge")
quarters <- c("FirstQuarter2020", "SecondQuarter2020")
sufix <- "_r-data.txt"

# Matriz de saida dos dados (com cabecalho)
outMatrix <- matrix(, nrow <- 0, ncol <- 11)
colnames(outMatrix) <- c("instanceType", "quarter",
	"q05", "q05LowerCI", "q05UpperCI",
	"q10", "q10LowerCI", "q10UpperCI",
	"q25", "q25LowerCI", "q25UpperCI")

# Percorrimento dos vetores de tipos de instancias e trimestres
for (instanceType in instanceTypes) {
	for (quarter in quarters) {

		# Leitura do arquivo CSV
		fileName <- paste("./Data/", instanceType, "_", quarter, sufix, sep="")
		csvData <- read.csv(file <- fileName, header <- FALSE, sep <- ";")

		# Obtem vetor de janelas com base no lance fixo (ate 12 horas)
		tmpData0 <- csvData$V7
		tmpData1 <- tmpData0[!is.na(tmpData0)]

		# Calculo do quantil 5% com seu respectivo intervalo de confiança 95%
		q05 <- unname(quantile(tmpData1, 0.05))
		q05CI <- quantileCI(tmpData1, 0.05, conf.level=.95)

		# Calculo do quantil 10% com seu respectivo intervalo de confiança 95%
		q10 <- unname(quantile(tmpData1, 0.1))
		q10CI <- quantileCI(tmpData1, 0.1, conf.level=.95)

		# Calculo do quantil 25% com seu respectivo intervalo de confiança 95%
		q25 <- unname(quantile(tmpData1, 0.25))
		q25CI <- quantileCI(tmpData1, 0.25, conf.level=.95)

		# Criacao do vetor com os valores resultantes
		outMatrix <- rbind(outMatrix,
			c(instanceType, quarter,
				q05, q05CI[1], q05CI[2],
				q10, q10CI[1], q10CI[2],
				q25, q25CI[1], q25CI[2]))
	}
}

# Escrita do arquivo de saida
outFileName <- paste("./Data/", "output.txt", sep="")
ret <- write.csv(outMatrix, file=outFileName)

### PROCESSAMENTO - FIM

### DEBUG - INICIO
#
# hist(tmpData1)
#
### DEBUG - FIM

