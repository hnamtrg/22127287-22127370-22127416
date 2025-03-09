pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Identify Changed Services') {
            steps {
                script {
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
                    def changedFiles = sh script: "git diff --name-only ${env.GIT_COMMIT}^ ${env.GIT_COMMIT}", returnStdout: true
                    def services = []
                    changedFiles.split('\n').each { file ->
                        def service_name = get_service_name(file)
                        if (service_name) {
                            services.add(service_name)
                        }
                    }
                    services = services.unique()
                    echo "Services with changes: ${services.join(', ')}"
                }
            }
        }
    }
}
