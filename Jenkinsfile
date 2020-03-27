
pipeline {

    agent {
        kubernetes {
          yamlFile 'slavePod.yaml'
        }
    }

    stages {
        stage('show Java version') {
            steps {
                sh 'java -version'
            }
        }

        stage('show Gradle version') {
            steps {
                sh 'gradle -version'
            }
        }

        stage('show docker info') {
            steps {
                sh 'docker info'
            }
        }

        stage('show kubernetes pods') {
             sh 'kubectl get pods'
        }
    }
}