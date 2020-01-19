pipeline {
    agent any 

    environment {
        ACR_CREDS = credentials('acr-credentials')
        DOCKER_REGISTRY = 'mydevopsacr001.azurecr.io'
        PROJECT_NAME = 'example-product-service'
        K8S_NAMESPACE = 'deployment'
        DOCKER_IMAGE = "${DOCKER_REGISTRY}/${PROJECT_NAME}:${env.BUILD_TIMESTAMP}.${env.BUILD_ID}"
        DOCKER_LATEST_IMAGE = "${DOCKER_REGISTRY}/${PROJECT_NAME}:latest"
        JIB_IMAGE = "net.thoughtworks/${PROJECT_NAME}:latest"
    }

    stages {
        stage('clone repository') {
            steps {
                sh 'echo "Cloning GitHub repository ..."'
                checkout scm
            }
        }

        stage('gradle clean build') {
            steps {
                // sh './gradlew clean build'
            }
        }
        stage('push image to ACR') {
            steps {
                sh """
                ./gradlew jibDockerBuild
                docker tag ${JIB_IMAGE} ${DOCKER_IMAGE}
                docker tag ${JIB_IMAGE} ${DOCKER_LATEST_IMAGE}
                docker login ${DOCKER_REGISTRY} -u ${ACR_CREDS_USR} -p ${ACR_CREDS_PSW}'
                docker push ${DOCKER_IMAGE}
                docker push ${DOCKER_LATEST_IMAGE}
                """
            }
        }

        stage('deploy to dev use AKS') {
            steps {
                sh 'echo "Using Kubectl to deploy application (redeploy) on AKS"'
                withKubeConfig(
                    caCertificate: '',
                    contextName: 'dev',
                    credentialsId: 'aks-credentials',
                    serverUrl: 'https://akslab-8b97af61.hcp.eastasia.azmk8s.io:443') {
                        // sh "kubectl set image $K8S_NAMESPACE/$PROJECT_NAME $PROJECT_NAME=$DOCKER_REGISTRY/$PROJECT_NAME:${env.BUILD_TIMESTAMP}.${env.BUILD_ID}"

                        sh """
                            sed \'s#{{ docker_image }}#${DOCKER_IMAGE}#g\' aks-shells/app-update-deploy.tpl.yaml > deploy/update-deploy.yaml
                        	kubectl apply -f aks-shells/update-deploy.yaml -n ${K8S_NAMESPACE}
                        """
                    }
            }
        }
    }
}
