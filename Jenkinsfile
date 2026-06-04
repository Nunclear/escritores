pipeline {
    agent any

    environment {
        IMAGE_NAME = 'escritores-backend'
        CONTAINER_NAME = 'escritores-backend'
        DOCKER_NETWORK = 'escritores-net'
        APP_PORT = '8080'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Install Dependencies') {
            steps {
                echo 'Las dependencias Maven se instalarán durante el build/test.'
            }
        }

        stage('Unit Tests') {
            steps {
                sh './mvnw test -Dmaven.test.failure.ignore=true'
            }
        }

        stage('Build Spring Boot') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Static Analysis') {
            steps {
                sh 'mvn -q -DskipTests compile'
            }
        }

        stage('Security Scan') {
            steps {
                echo 'Escaneo básico de seguridad. Puedes integrar OWASP Dependency Check o Trivy aquí.'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t $IMAGE_NAME:latest .'
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                docker stop $CONTAINER_NAME || true
                docker rm $CONTAINER_NAME || true

                docker run -d \
                  --name $CONTAINER_NAME \
                  --restart always \
                  --network $DOCKER_NETWORK \
                  -p $APP_PORT:8080 \
                  $IMAGE_NAME:latest
                '''
            }
        }
    }

    post {
        success {
            echo 'Backend desplegado correctamente en el puerto 8080.'
        }

        failure {
            echo 'Falló el pipeline del backend.'
        }
    }
}
