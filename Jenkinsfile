#!/usr/bin/env groovy

pipeline {
    agent { label '22127287-22127416-22127370' }
    environment {
        GITHUB_TOKEN = credentials('jenkins')
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh "git fetch origin main:refs/remotes/origin/main" // Fetch main vào origin/main
            }
        }
        stage('Check Changes') {
            steps {
                script {
                    def changedServices = getChangedServices()
                    if (changedServices.isEmpty()) {
                        echo "No changes detected in service directories. Skipping pipeline."
                        currentBuild.result = 'SUCCESS'
                        return
                    } else {
                        echo "Changed services detected: ${changedServices}"
                    }
                }
            }
        }

        stage('Test Services') {
            when {
                expression { !getChangedServices().isEmpty() }
            }
            steps {
                script {
                    def changedServices = getChangedServices()
                    for (service in changedServices) {
                        dir(service) {
                            try {
                                echo "Running tests for ${service}"
                                sh "mvn test"
                                sh "mvn jacoco:report"
                                sh "if [ -d target/surefire-reports ]; then zip -r test-results.zip target/surefire-reports/; else echo 'No test results to zip'; fi"
                                archiveArtifacts artifacts: 'target/surefire-reports/*.xml', allowEmptyArchive: true
                                archiveArtifacts artifacts: 'target/site/jacoco/**', allowEmptyArchive: true
                                archiveArtifacts artifacts: 'test-results.zip', allowEmptyArchive: true
                                junit 'target/surefire-reports/*.xml'
                                jacoco(
                                    execPattern: 'target/jacoco.exec',
                                    classPattern: 'target/classes',
                                    sourcePattern: 'src/main/java',
                                    exclusionPattern: ''
                                )
                            } catch (Exception e) {
                                echo "Tests failed for ${service}: ${e.message}"
                                currentBuild.result = 'FAILURE'
                            }
                        }
                    }
                }
            }
        }

        stage('Build Services') {
            when {
                expression { !getChangedServices().isEmpty() }
            }
            steps {
                script {
                    def changedServices = getChangedServices()
                    for (service in changedServices) {
                        dir(service) {
                            try {
                                echo "Building ${service}"
                                sh "mvn package -DskipTests"
                                archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
                            } catch (Exception e) {
                                echo "Build failed for ${service}: ${e.message}"
                                currentBuild.result = 'FAILURE'
                            }
                        }
                    }
                }
            }
        }
    }
}

def getChangedServices() {
    def baseBranch = env.CHANGE_TARGET ?: 'main'
    def changedFiles = sh(returnStdout: true, script: "git diff --name-only origin/${baseBranch} HEAD").trim().split("\n")
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
