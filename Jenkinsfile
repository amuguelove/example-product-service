def IMAGE_NAME = 'ccr.ccs.tencentyun.com/my-registry/example-product-service'
def LOCAL_IMAGE = 'net.thoughtworks/example-product-service:latest'
pipeline {
    agent any 

    environment {
        DOCKER_CREDENTIALS = credentials('DOCKER_CREDENTIALS')
        K8S_NAMESPACE = 'development'
        IMAG_TAG = ${env.BUILD_ID}
        //IMAG_TAG = env.GIT_COMMIT.substring(0,7)
    }

    options {
        timeout(time: 10, unit: 'MINUTES')
    }

    stages {
//         stage('clone repository') {
//             steps {
//                 sh 'echo "Cloning GitHub repository ..."'
//                 checkout scm
//             }
//         }

        stage('build Project') {
            steps {
                sh """
                sudo ./gradlew clean build
                """
            }
        }

        stage('push image') {
            steps {
                sh """
                ./gradlew jibDockerBuild
                docker tag ${LOCAL_IMAGE} ${IMAGE_NAME}

                docker login ccr.ccs.tencentyun.com -u ${DOCKER_CREDENTIALS_USR} -p ${DOCKER_CREDENTIALS_PSW}
                docker push ${IMAGE_NAME}
                """
            }
        }

        stage('deploy to dev') {
            steps {
                deployToStage('dev', 'dev')
            }
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
          sed -i '' -e 's/example-product-service:latest/example-product-service:${IMAG_TAG}/g'  './deploy/tencent/app_${stage}.yaml'
          kubectl -n ${ns ?: "default"} apply -f './deploy/tencent/app_${stage}.yaml' --force
        """
    }
}
