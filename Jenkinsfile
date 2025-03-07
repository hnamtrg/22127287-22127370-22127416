#!/usr/bin/env groovy

pipeline {
    agent any
    environment {
        GITHUB_TOKEN = credentials('github-token')
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // Stage Test cho từng service
        stage('Test Services') {
            when {
                expression {
                    def changedServices = getChangedServices()
                    return !changedServices.isEmpty()
                }
            }
            steps {
                script {
                    def changedServices = getChangedServices()
                    for (service in changedServices) {
                        dir(service) {
                            echo "Running tests for ${service}"
                            sh "mvn test"
                            sh "mvn jacoco:report"
                            junit 'target/surefire-reports/*.xml'
                            archiveArtifacts artifacts: 'target/surefire-reports/*.xml', allowEmptyArchive: true
                            archiveArtifacts artifacts: 'target/site/jacoco/**', allowEmptyArchive: true
                            jacoco(
                                execPattern: 'target/jacoco.exec',
                                classPattern: 'target/classes',
                                sourcePattern: 'src/main/java',
                                exclusionPattern: ''
                            )
                        }
                    }
                }
            }
        }

        // Stage Build cho từng service
        stage('Build Services') {
            when {
                expression {
                    def changedServices = getChangedServices()
                    return !changedServices.isEmpty()
                }
            }
            steps {
                script {
                    def changedServices = getChangedServices()
                    for (service in changedServices) {
                        dir(service) {
                            echo "Building ${service}"
                            sh "mvn package -DskipTests"
                            archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
                            sh "zip -r test-results.zip target/surefire-reports/"
                            archiveArtifacts artifacts: 'test-results.zip', allowEmptyArchive: true
                        }
                    }
                }
            }
        }
    }
    post {
        success {
            script {
                notifyGitHub("success", "ci/jenkins", "Build and tests passed successfully for changed services.")
            }
        }
        failure {
            script {
                notifyGitHub("failure", "ci/jenkins", "Build or tests failed for changed services.")
            }
        }
    }
}

// Hàm phát hiện service thay đổi
def getChangedServices() {
    def changedFiles = sh(returnStdout: true, script: "git diff --name-only HEAD^ HEAD").trim().split("\n")
    def services = []
    def serviceDirs = ["spring-petclinic-config-server", "spring-petclinic-discovery-server", "spring-petclinic-vets-service", "spring-petclinic-customers-service", "spring-petclinic-visits-service", "spring-petclinic-apigateway", "spring-petclinic-genai-service", "spring-petclinic-ui"]
    for (file in changedFiles) {
        for (dir in serviceDirs) {
            if (file.startsWith(dir + "/")) {
                services.add(dir)
                break
            }
        }
    }
    return services.unique()
}

// Hàm gửi trạng thái về GitHub
def notifyGitHub(String state, String context, String description) {
    def commitSha = env.GIT_COMMIT
    def repoUrl = "https://api.github.com/repos/YOUR_USERNAME/spring-petclinic-microservices/statuses/${commitSha}"
    def buildUrl = "${env.BUILD_URL}"
    def payload = """{
        "state": "${state}",
        "target_url": "${buildUrl}",
        "description": "${description}",
        "context": "${context}"
    }"""
    sh """
        curl -X POST \
        -H "Authorization: token ${env.GITHUB_TOKEN}" \
        -H "Content-Type: application/json" \
        -d '${payload}' \
        "${repoUrl}"
    """
}
