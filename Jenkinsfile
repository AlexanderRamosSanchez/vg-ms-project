pipeline {
    agent any
    environment {
        R2DBC_URL = credentials('R2DBC_URL')
        R2DBC_USERNAME = credentials('R2DBC_USERNAME')
        R2DBC_PASSWORD = credentials('R2DBC_PASSWORD')
    }
    stages {
        stage('Clonar Repositorio') {
            steps {
                git branch: 'vg-ms-information',
                url: 'https://github.com/AlexanderRamosSanchez/vg-ms-project.git'
            }
        }
        stage('Compilar con Maven') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Ejecutar Pruebas Unitarias') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Generar Artefacto .jar') {
            steps {
                sh 'mvn package'
            }
        }
        stage('Análisis con SonarCloud') {
            steps {
                script {
                    withSonarQubeEnv('SonarCloud') {
                        withCredentials([
                            string(credentialsId: 'SONAR_PROJECT_KEY', variable: 'SONAR_PROJECT_KEY'),
                            string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')
                        ]) {
                            sh "mvn sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dsonar.login=${SONAR_TOKEN} -X"
                        }
                    }
                }
            }
        }
    }
    post {
        success {
            echo 'Pipeline completado con éxito.'
        }
        failure {
            echo 'Pipeline fallido.'
        }
    }
}