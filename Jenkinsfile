def IMAGE_NAME = 'ccr.ccs.tencentyun.com/my-registry/example-product-service'

pipeline {

    agent {
        kubernetes {
          yamlFile 'slavePod.yaml'
        }
    }

    environment {
        DOCKER_CREDENTIALS = credentials('DOCKER_CREDENTIALS')
//         IMAG_TAG = "${env.BUILD_ID}"
        IMAG_TAG = env.GIT_COMMIT.substring(0,7)
    }

    options {
        timeout(time: 10, unit: 'MINUTES')
    }

    stages {

        stage('build Project') {
            steps {
                sh """
                ./gradlew clean build
                """
            }
        }

        stage('push image') {
            steps {
                sh "docker images --filter=reference=\"${IMAGE_NAME}:*\" -q | xargs docker rmi --force || true"
                sh "docker login ccr.ccs.tencentyun.com -u ${DOCKER_CREDENTIALS_USR} -p ${DOCKER_CREDENTIALS_PSW}"
                sh "docker build -t ${IMAGE_NAME}:${IMAG_TAG} ."
                sh "docker push ${IMAGE_NAME}:${IMAG_TAG}"
            }
        }

        stage('deploy to dev') {
            steps {
                deployToStage('dev', 'dev')
            }
        }
    }

    post {
            always {
                publishHTML target: [
                        allowMissing         : false,
                        alwaysLinkToLastBuild: true,
                        keepAll              : true,
                        reportDir            : 'build/reports/jacoco/test/html',
                        reportFiles          : 'index.html',
                        reportName           : 'Jacoco Report'
                ]

                publishHTML target: [
                        allowMissing         : false,
                        alwaysLinkToLastBuild: true,
                        keepAll              : true,
                        reportDir            : 'build/reports/tests/test/',
                        reportFiles          : 'index.html',
                        reportName           : 'Test Report'
                ]
            }
        }
}

def deployToStage(ns, stage) {
    withKubeConfig(
        caCertificate: '',
        contextName: '',
        credentialsId: 'k8s-credentials',
        serverUrl: 'https://cls-2vcqd9cl.ccs.tencent-cloud.com'
    ) {
        sh """
        sed \'s/example-product-service:latest/example-product-service:${IMAG_TAG}/g\' './deploy/tencent/app-${stage}.yaml'
        kubectl -n ${ns ?: "default"} apply -f './deploy/tencent/app-${stage}.yaml' --force
        """
    }
}