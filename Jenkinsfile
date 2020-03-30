def label = "jnlp-slave-${UUID.randomUUID().toString()}"

podTemplate(
  label: label,
  cloud: 'kubernetes',
  containers: [
    containerTemplate(name: 'jnlp', image: 'jicki/jenkins-jnlp:gradle5.6', ttyEnabled: true)
  ],
  serviceAccount: 'jenkins',
  volumes: [
    hostPathVolume(mountPath: '/usr/bin/docker', hostPath: '/usr/bin/docker'),
    hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
    hostPathVolume(mountPath: '/root/.gradle', hostPath: '/opt/data/gradle'),
    hostPathVolume(mountPath: '/usr/bin/kubectl', hostPath: '/usr/bin/kubectl'),
    hostPathVolume(mountPath: '/root/.kube/', hostPath: '/root/.kube/'),
  ]
) {
  node(label) {
    def IMAGE_NAME = 'ccr.ccs.tencentyun.com/my-registry/example-product-service'
    def IMAG_TAG = "${env.Build_TIMESTAMP}.${env.BUILD_ID}"

    stage('clone repository') {
        checkout([
        $class: 'GitSCM',
        branches: [[name: '*/master']],
        doGenerateSubmoduleConfigurations: false,
        extensions: [[$class: 'CleanBeforeCheckout']],
        submoduleCfg: [],
        userRemoteConfigs: [[credentialsId: 'github', url: 'https://github.com/amuguelove/example-product-service.git']]])
        echo 'Checkout'
    }

    stage('build project') {
        sh "LOCAL_ADDRESS=172.27.0.3 ./gradlew clean build -Dorg.gradle.daemon=false"

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
                reportDir            : 'build/reports/tests/integrationTest',
                reportFiles          : 'index.html',
                reportName           : 'Test Report'
        ]
    }

    stage('push image') {
        withCredentials([[$class: 'UsernamePasswordMultiBinding',
          credentialsId: 'DOCKER_CREDENTIALS',
          usernameVariable: 'DOCKER_CREDENTIALS_USR',
          passwordVariable: 'DOCKER_CREDENTIALS_PSW']]) {
          sh """
            docker login ccr.ccs.tencentyun.com -u ${DOCKER_CREDENTIALS_USR} -p ${DOCKER_CREDENTIALS_PSW}
            docker build -t ${IMAGE_NAME}:${IMAG_TAG} .
            docker push ${IMAGE_NAME}:${IMAG_TAG}
            """
        }
    }

    stage('deploy to dev') {
      deployToStage('dev', 'dev', IMAG_TAG)
    }

  }
}

def deployToStage(ns, stage, imageTag) {
    withKubeConfig(
        caCertificate: '',
        contextName: '',
        credentialsId: 'k8s-credentials',
        serverUrl: 'https://cls-2vcqd9cl.ccs.tencent-cloud.com'
    ) {
        sh "echo 172.27.0.5 cls-2vcqd9cl.ccs.tencent-cloud.com >> /etc/hosts"

        sh """
        sed \'s/example-product-service:latest/example-product-service:${imageTag}/g\' './deploy/tencent/app-${stage}.yaml'
        kubectl -n ${ns ?: "default"} apply -f './deploy/tencent/app-${stage}.yaml' --force
        """
    }
}