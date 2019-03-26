pipeline {
  agent any
  stages {
    stage('checkout project') {
      steps {
        checkout scm
      }
    }
    stage('build') {
      steps {
        sh './mvnw install dockerfile:build'
      }
    }
    stage('deploy') {
      steps {
        sh 'docker ps -f name=n26-transaction-service -q | xargs --no-run-if-empty docker container stop'
        sh 'docker container ls -a -fname=n26-transaction-service -q | xargs -r docker container rm'
        sh 'docker run --name n26-transaction-service -p 8888:8080 -t springio/n26-transaction-service'
        sh 'docker logs n26-transaction-service'
      }
    }
  }
}