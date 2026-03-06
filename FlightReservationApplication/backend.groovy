pipeline{
    agent any 
    stages{
        stage('Code-pull'){
            steps{
                git branch: 'main', url: 'https://github.com/anuppogade/flight-reservation-app.git'
            }
        }
        stage('Code-build'){
            steps{
                sh '''
                cd FlightReservationApplication
                mvn clean package
                '''
            }
        }
        stage('quality-check'){
            steps{
                withSonarQubeEnv(installationName: 'sonar', credentialsId: 'Sonar-token') {
                    sh '''
                     cd FlightReservationApplication
                     mvn sonar:sonar -Dsonar.projectKey=flight-reserve-backend
                    '''
                    }
                        
                }
            }
        stage('Dockerbuild'){
            steps{
                sh '''
                    cd FlightReservationApplication
                    docker build -t anuppogade/fligh-reservation-app:latest .
                    docker push anuppogade/fligh-reservation-demo:latest
                    docker rmi anuppogade/fligh-reservation-demo:latest

                '''
            }
        }
        stage('Deploy'){
            steps{
                sh '''
                cd FlightReservationApplication
                kubectl apply -f k8s/
                '''
            }
        }
        }
    }
