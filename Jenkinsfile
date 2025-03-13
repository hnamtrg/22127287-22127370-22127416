def get_service_name(file_path) {
    def parts = file_path.split('/')
    if (parts.size() < 1) {
        return null
    }
    def directory_name = parts[0]
    if (directory_name.startsWith("spring-petclinic-")) {
        return directory_name.substring("spring-petclinic-".length())
    } else {
        return null
    }
}

def parseJacocoCoverage(String xmlContent) {
    def coverage = [:]
    def totalMissed = 0.0
    def totalCovered = 0.0

    def instructionCounters = (xmlContent =~ /<counter type="INSTRUCTION" missed="([^"]*)" covered="([^"]*)"/)
    instructionCounters.each { match ->
        totalMissed += match[1].toFloat()
        totalCovered += match[2].toFloat()
    }

    coverage.instruction = totalCovered / (totalMissed + totalCovered)
    return coverage
}

pipeline {
    agent { label '22127287-22127416-22127370' }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Identify Changed Services') {
            steps {
                script {
                    def changedFiles = sh(script: "git diff --name-only ${env.GIT_COMMIT}^ ${env.GIT_COMMIT}", returnStdout: true)
                    def services = []
                    changedFiles.split('\n').each { file ->
                        def service_name = get_service_name(file)
                        if (service_name) {
                            services.add(service_name)
                        }
                    }
                    services = services.unique()
                    echo "Services with changes: ${services.join(', ')}"
                    env.CHANGED_SERVICES = services.join(',')
                }
            }
        }
        stage('Test and Coverage') {
            when {
                expression { env.CHANGED_SERVICES != '' }
            }
            steps {
                script {
                    def services = env.CHANGED_SERVICES.split(',')
                    services.each { service ->
                        echo "Running tests for service: ${service}"
                        dir("spring-petclinic-${service}") {
                            sh "mvn clean test jacoco:report"
                            sh "zip -r ${service}-test-results.zip target/surefire-reports/ target/site/jacoco/"
                            archiveArtifacts artifacts: "${service}-test-results.zip", allowEmptyArchive: false

                            if (!fileExists("target/site/jacoco/jacoco.xml")) {
                                error("JaCoCo report file (jacoco.xml) not found!")
                            }
                            def jacocoXml = readFile(file: "target/site/jacoco/jacoco.xml")
                            def coverage = parseJacocoCoverage(jacocoXml)

                            def minCoverage = 0.70
                            def totalInstructionCoverage = coverage.instruction
                            if (totalInstructionCoverage >= minCoverage) {
                                echo "Test coverage is sufficient! Instruction Coverage: ${totalInstructionCoverage*100}%"
                            } else {
                                error("Test coverage is insufficient! Required: 70%, Got: ${totalInstructionCoverage*100}%")
                            }
                        }
                    }
                }
            }
        }
        stage('Build') {
            when {
                expression { env.CHANGED_SERVICES != '' }
            }
            steps {
                script {
                    def services = env.CHANGED_SERVICES.split(',')
                    services.each { service ->
                        echo "Building service without tests: ${service}"
                        dir("spring-petclinic-${service}") {
                            sh "mvn clean package -DskipTests"
                            archiveArtifacts artifacts: "target/*.jar", allowEmptyArchive: false
                        }
                    }
                }
            }
        }
    }
}
