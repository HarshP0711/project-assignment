#loading the library
library(dplyr)
library(ggplot2)
library(rpart)
library(rpart.plot)
library(caret)
library(caTools)
library(ROCR)
install.packages('ROCR')


## Load the data
setwd("Downloads")
list.files()
data <- read.csv("IDS 472 hw 5 data.csv")
View(data)

#1a
nrow(data)
ncol(data)

#1b
which(is.na(data))
sum(is.na(data))
summary(data)


#1c

OutLiers = boxplot.stats(data$Age)$out
AgenoOut = ifelse(data$Age %in% OutLiers, NA, data$Age)
boxplot(data$Age)
axis(2, at = seq(0, 70, 5))

OutLiers1 = boxplot.stats(data$Notice.period)$out
NoticenoOut = ifelse(data$Notice.period %in% OutLiers1, NA, data$Notice.period)
boxplot(data$Notice.period)
axis(2, at = seq(0, 120, 5))

OutLiers2 = boxplot.stats(data$Duration.to.accept.offer)$out
DurationnoOut = ifelse(data$Duration.to.accept.offer %in% OutLiers2, NA, data$Duration.to.accept.offer)
boxplot(data$Duration.to.accept.offer)
axis(2, at = seq(-100, 80, 5))

OutLiers3 = boxplot.stats(data$Pecent.hike.expected.in.CTC)$out
PHikenoOut = ifelse(data$Pecent.hike.expected.in.CTC %in% OutLiers3, NA, data$Pecent.hike.expected.in.CTC)
boxplot(data$Pecent.hike.expected.in.CTC)
axis(2, at = seq(-50, 300, 5))

OutLiers4 = boxplot.stats(data$Percent.difference.CTC)$out
PDifnoOut = ifelse(data$Percent.difference.CTC %in% OutLiers4, NA, data$Percent.difference.CTC)
boxplot(data$Percent.difference.CTC)
axis(2, at = seq(-30, 50, 2))

OutLiers5 = boxplot.stats(data$Percent.hike.offered.in.CTC)$out
DurationnoOut = ifelse(data$Percent.hike.offered.in.CTC %in% OutLiers5, NA, data$Percent.hike.offered.in.CTC)
boxplot(data$Percent.hike.offered.in.CTC)
axis(2, at = seq(-30, 100, 5))

NewData = subset(data , Age < 45
                 & Notice.period < 100
                 & Duration.to.accept.offer < 70
                 & Pecent.hike.expected.in.CTC < 100
                 & Pecent.hike.expected.in.CTC > -15
                 & Percent.difference.CTC < 12
                 & Percent.difference.CTC > -22
                 & Percent.hike.offered.in.CTC < 85
                 & Percent.hike.offered.in.CTC > -20)
View(NewData)

#summary(data)
#sd(data)
#hist(data)
#hist(log(data))


#1d
chisq.test(NewData$DOJ.Extended, NewData$Status)


#1e
#a <- data$Pecent.hike.expected.in.CTC
#b <- data$Percent.hike.offered.in.CTC
#difference_hike_expectation = a-b

t.test(NewData$Pecent.hike.expected.in.CTC, NewData$Percent.hike.offered.in.CTC, var.equal = TRUE)

##2a Creating testing and training dataset
set.seed(123)
train_index <- sample(2, 1338, prob = c(0.7, 0.3), replace = T)

train <- data[train_index == 1, ]
test <- data[train_index == 2, ]
summary(train)
summary(test)
print(table(train$Status)[2]/nrow(train))
print(table(test$Status)[2]/nrow(test))

## Making the status variable to 0/1
train$Status<-ifelse(train$Status=="Joined",1,0)
train

test$Status<-ifelse(test$Status=="Joined",1,0)
test

#3a
DT_model1 <- rpart(Status ~ . , data = train)
DT_model1

#3b
DT_model2 <- rpart(Status ~ . , data = train, control = rpart.control(minibucket = 500))
DT_model2

#3c
library(rpart.plot)
rpart.plot(DT_model2)

#3d
trainpreds <- predict(DT_model2, train, type = "prob") [, 2]
testpreds <- predict(DT_model2, train, type = "prob") [, 2]

install.packages('PRROC')
library(PRROC)
data <- data[complete.cases(data), ]
roc_train <- roc.curve(scores.class0 = trainpreds, weights.class0 = as.numeric(as.character(train$Status)), curve = T)

prcurve_train <- pr.curve(scores.class0 = trainpreds, weights.class0 = as.numeric(as.character(train$Status)), curve = T)

roc_test <- roc.curve(scores.class0 = testpreds, weights.class0 = as.numeric(as.character(test$Status)), curve = T)
prcurve_test <- pr.curve(scores.class0 = testpreds, weights.class0 = as.numeric(as.character(test$Status)), curve = T)

plot(roc_train, main = "ROC curve For Train Data")
plot(roc_test, main = "ROC curve For Test data")
plot(prcurve_train, main = "PR curve For Train data")
plot(prcurve_test, main = "ROC curve For Test data")

#3e testing data 
probs <- predict(DT_model1,test, type = "class")
predictions <- ifelse(probs >= 0.6, 1, 0)
table.output <- table(actual = test$Status, predicted = predictions)
tn = table.output[1,1]
fp = table.output[1,2]
fn = table.output[2,1]
tp = table.output[2,2]

## Precision 
tp / (tp + fp)
## Recall 
tp / (tp + fn)
print(paste("The precision is", Precision, sep = ""))
print(paste("The recall is", Recall, sep = ""))
## training data
probs <- predict(DT_model1,train, type = "response")
predictions <- ifelse(probs >= 0.6, 1, 0)
table.output = table(train$Status, predictions)
tn = table.output[1,1]
fp = table.output[1,2]
fn = table.output[2,1]
tp = table.output[2,2]
## Precision 
tp / (tp + fp)
## Recall 
tp / (tp + fn)
print(paste("The precision is", Precision, sep = ""))
print(paste("The recall is", Recall, sep = ""))

##performance(data, acc, measure = "acc")
#acc.perf = performance(trainpreds, measure = "acc")
#plot(acc.perf)

#3f


#4a
glm_model <- glm(as.factor(Status) ~., data=train, family= "binomial")
summary(glm_model)

#4b
trainpreds <- predict(DT_model2, train, type = "prob") [, 2]
testpreds <- predict(DT_model2, train, type = "prob") [, 2]

roc_train <- roc.curve(scores.class0 = trainpreds, weights.class0 = as.numeric(as.character(train$Status)), curve = T)


prcurve_train <- pr.curve(scores.class0 = trainpreds, weights.class0 = as.numeric(as.character(train$Status)), curve = T)

roc_test <- roc.curve(scores.class0 = testpreds, weights.class0 = as.numeric(as.character(test$Status)), curve = T)
prcurve_test <- pr.curve(scores.class0 = testpreds, weights.class0 = as.numeric(as.character(test$Status)), curve = T)

plot(roc_train, main = "ROC curve For Train Data")
plot(roc_test, main = "ROC curve For Test data")
plot(prcurve_train, main = "PR curve For Train data")
plot(prcurve_test, main = "ROC curve For Test data")

###4c Testing data 
probs <- predict(glm_model,test, type = "response")
predictions <- ifelse(probs >= 0.6, 1, 0)
table.output = table(test$Status, predictions)
tn = table.output[1,1]
fp = table.output[1,2]
fn = table.output[2,1]
tp = table.output[2,2]
## Precision 
tp / (tp + fp)
## Recall 
tp / (tp + fn)
print(paste("The precision is", Precision, sep = ""))
print(paste("The recall is", Recall, sep = ""))

## Training data 
trainprobs <- predict(glm_model,train, type = "response")
predictions <- ifelse(probs >= 0.6, 1, 0)
table.output = table(train$Status, predictions)
tn = table.output[1,1]
fp = table.output[1,2]
fn = table.output[2,1]
tp = table.output[2,2]
## Precision 
tp / (tp + fp)
## Recall 
tp / (tp + fn)
print(paste("The precision is", Precision, sep = ""))
print(paste("The recall is", Recall, sep = ""))

