node {
 
    withMaven(maven:'maven') {
 
        stage('Checkout') {
            git url: 'https://github.com/roanbrasil/n26-transaction-service.git', credentialsId: 'github-roanbrasil', branch: 'master'
        }
 
        stage('Build') {
            sh 'mvn clean install'
 
            def pom = readMavenPom file:'pom.xml'
            print pom.version
            env.version = pom.version
        }
 
        stage('Image') {
            dir ('n26-transaction-service') {
                def app = docker.build "localhost:8080/n26-transaction-service:${env.version}"
                app.push()
            }
        }
 
        stage ('Run') {
            docker.image("localhost:8080/n26-transaction-service:${env.version}").run('-p 8080:8080 -t springio/n26-transaction-service --name n26-transaction-service')
        }
 
        stage ('Final') {
            build job: 'n26-service-pipeline', wait: false
        }      
 
    }
 
}

