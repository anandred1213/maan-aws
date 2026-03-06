pipeline {
    agent any

    tools {
        maven 'Maven-3.8'
        jdk 'Java-21'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code...'
                checkout scm
            }
        }

        stage('Build Services') {
            parallel {
                stage('Product Service') {
                    steps {
                        dir('product-microservice') {
                            bat 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('Revision Service') {
                    steps {
                        dir('revision') {
                            bat 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('Payment Service') {
                    steps {
                        dir('payment') {
                            bat 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('Order Service') {
                    steps {
                        dir('order-microservice') {
                            bat 'mvn clean package -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'
                dir('product-microservice') {
                    bat 'mvn test'
                }
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo '✅ Build successful!'
        }
        failure {
            echo '❌ Build failed!'
        }
        stage('test'){
            steps{
            echo 'running the tests'
            bat  'mvn test'
            }
        }
        stage('package'){
            steps{
                echo 'packaging the application'
                bat 'mvn package'
            }
        }
        post{
        always{
             junit 'target/surefire-reports/*.xml
        }
        }
    }
}
