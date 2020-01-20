pipeline {
    agent any 

    environment {
        ACR_CREDS = credentials('acr-credentials')
        DOCKER_REGISTRY = 'mydevopsacr001.azurecr.io'
        PROJECT_NAME = 'example-product-service'
        K8S_NAMESPACE = 'development'
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

        stage('Create Docker Environment') {
            steps {
                sh """
                sudo /usr/local/bin/docker-compose -f src/test/resources/docker-compose.yml down
                sudo /usr/local/bin/docker-compose -f src/test/resources/docker-compose.yml up -d
                sleep 10
                """
            }
        }

        stage('Build Project') {
            steps {
                sh """
                sudo ./gradlew clean build -Dorg.gradle.daemon=false
                """
            }
        }

        stage('Destroy Docker Environment') {
            steps {
                sh """
                sudo /usr/local/bin/docker-compose -f src/test/resources/docker-compose.yml down
                """
            }
        }

        stage('push image to ACR') {
            steps {
                sh """
                sudo ./gradlew jibDockerBuild
                sudo docker tag ${JIB_IMAGE} ${DOCKER_IMAGE}
                sudo docker tag ${JIB_IMAGE} ${DOCKER_LATEST_IMAGE}
                sudo docker login ${DOCKER_REGISTRY} -u ${ACR_CREDS_USR} -p ${ACR_CREDS_PSW}
                sudo docker push ${DOCKER_IMAGE}
                sudo docker push ${DOCKER_LATEST_IMAGE}
                """
            }
        }

        stage('deploy to dev use AKS') {
            steps {
                sh 'echo "Using Kubectl to deploy application on AKS"'
                withKubeConfig(
                    caCertificate: '',
                    contextName: 'dev',
                    credentialsId: 'aks-credentials',
                    serverUrl: 'https://akslab-8b97af61.hcp.eastasia.azmk8s.io:443') {
                        sh """
                        sed \'s#{{ docker_image }}#${DOCKER_IMAGE}#g\' aks-shells/app-update-deploy.tpl.yaml > aks-shells/update-deploy.yaml
                        sudo /home/labuser/aztools/kubectl apply -f aks-shells/update-deploy.yaml -n ${K8S_NAMESPACE}
                        """
                    }
            }
        }
    }
}
