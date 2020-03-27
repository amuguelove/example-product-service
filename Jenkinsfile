pipeline {
  agent {
    kubernetes {
      yamlFile 'slavePod.yaml'
    }
  }

  stages {
    stage('show Java version') {
        sh 'java -version'
    }
    stage('show Gradle version') {
        sh 'gradle -version'
    }
    stage('show docker info') {
        sh 'docker info'
    }
    stage('show kubernetes pods') {
        sh 'kubectl get pods'
    }
  }
}