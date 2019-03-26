pipeline {
  agent any 
  stages {
    stage('checkout project') {
      steps {
        checkout scm
      }
    }
     stage ('build'){
            steps {
		sh './mvnw install dockerfile:build'
             }
    }
    stage ('deploy'){
            steps {
		sh 'docker run -n n26-transaction-service -p 8888:8080 -t springio/n26-transaction-service'
		sh 'docker logs n26-transaction-service'
             }
    }
  }
}
