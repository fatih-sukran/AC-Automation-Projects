#!groovy

pipeline {
    agent any

    options {
        timeout(time: 1, unit: 'HOURS') // En fazla 1 saat sürer
    }

    tools {
        maven "maven"
    }

    parameters {
        string name: "SUITE_ID", defaultValue: "1", description: "Lighthouse Suite ID"
    }

    environment {
        SUITE_ID='${params.SUITE_ID}'
    }

    stages {
        stage("Run Automation Tests") {
            steps {
                script {
                    sh script: "mvn clean test", returnStatus: true
                }
            }
        }

        stage("Generate Allure Report") {
            steps {
                allure includeProperties:
                        false,
                        jdk: '',
                        results: [[path: 'allure-results']]
            }
        }
    }

}
