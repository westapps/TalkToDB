def deployEnvType = ["production"]

def printEnvironment() {
    echo "Printing Environment Variables:"
    env.each { key, value ->
        echo "$key = $value"
    }
}

pipeline {
    agent any
    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'The name of the branch to build and deploy')
        choice(name: 'ENV_TYPE', choices: deployEnvType, description: 'Choose the environment to build and deploy into')
    }
    environment {
        //Git
        GIT_URL             = 'https://github.com/westapps/TalkToDB.git'
        GIT_CREDENTIALS     = 'github-credentials'
        //AWS
        AWS_DEFAULT_REGION  = 'ap-southeast-2'
        APPLICATION_NAME    = 'talktodb'
        AWS_ECR_REGISTRY     = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com"
        IMAGE_REPO_NAME      = "scala/${APPLICATION_NAME}"
        IMAGE_TAG            = 'latest'
        ECR_URI              = "${AWS_ECR_REGISTRY}/${IMAGE_REPO_NAME}"
        EC2_USER_AT_TALKTODB_INSTANCE = 'ec2-user@ip-172-31-44-201.ap-southeast-2.compute.internal'
        TALKTODB_EC2_SSH_CREDENTIALS = 'talktodb-ec2-ssh-key-id'
        //SBT
        TEAM                = 'westapps'
        ENV_NAME            = 'ai'
        ECR_REPO_PREFIX     = "${TEAM}"
        DOCKER_FILE_NAME    = 'Dockerfile'
        CODE_BASE_PATH      = "."
        SBT_BUILD_VER        = "sbt 1.7.2"
        SBT_TEST_FLAGS       = " "
        SBT_TEST_CMD         = "clean test scapegoat" //coverage coverageReport sonarScan
        SBT_DOCKER_IMAGE     = "sbtscala/scala-sbt:eclipse-temurin-jammy-21.0.2_13_1.9.8_2.13.12"
        SBT_BUILD_FLAGS      = "-Dsbt.log.noformat=true"
        SBT_BUILD_CMD        = "clean universal:packageZipTarball"
        //
        SOURCE_DIRS          = "${CODE_BASE_PATH}/src/main" // Comma-separated list of directories that contain source code to analyze
        EXCLUSION_DIRS       = "**/*Bean.scala,**/*DTO.scala"
        LIBRARY_DIRS         = "${HOME}/.ivy2"
        JAR_DIRS             = "${CODE_BASE_PATH}"
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '40'))
        timestamps()
        disableConcurrentBuilds()
    }
    stages {
        stage('Check Out Git Repo') {
            steps {
                git branch: 'main', 
                url: "${GIT_URL}",
                credentialsId: "${GIT_CREDENTIALS}"
            }
        }    
        stage('Print env') {
            steps {
                script { //todo: this block of script did not run, can noe display ${BUILD_NUMBER}
                    currentBuild.displayName = "#${env.BUILD_NUMBER}:${env.BRANCH_NAME}"
                    currentBuild.description = "ENV_TYPE: <b>${ENV_TYPE}</b>"
                    printEnvironment()
                }
            }
        }
        stage('Test Code') {
            agent {
                docker {
                    image SBT_DOCKER_IMAGE
                    args "-v /var/run/docker.sock:/var/run/docker.sock -v ${HOME}/.ivy2:/root/ -u root"
                }
            }
            steps {
                sh("sbt ${SBT_TEST_FLAGS} ${SBT_TEST_CMD}")
            }
        }
        stage('Build Code') {
            agent {
                docker {
                    image SBT_DOCKER_IMAGE
                    args "-v /var/run/docker.sock:/var/run/docker.sock -v ${HOME}/.ivy2:/root/ -u root"
                }
            }
            steps {
                sh("sbt ${SBT_BUILD_FLAGS} ${SBT_BUILD_CMD}")
                stash(name: "artifact", includes: "target/**")
            }
        }
        stage('Authenticate with AWS ECR') {
            steps {
                sh '''
                    aws ecr get-login-password --region $AWS_DEFAULT_REGION | \
                    docker login --username AWS --password-stdin $AWS_ECR_REGISTRY
                '''
            }
        }
        stage('Build Container') {
            steps {
                script {
                    unstash 'artifact'
                    sh "docker build --build-arg ENV_TYPE=${params.ENV_TYPE} --network=host -t $AWS_ECR_REGISTRY/$IMAGE_REPO_NAME:$IMAGE_TAG ."
                }
            }
        }
        stage('Push Docker Image to AWS ECR') {
            steps {
                // Push the Docker image to AWS ECR
                sh '''
                    docker push $AWS_ECR_REGISTRY/$IMAGE_REPO_NAME:$IMAGE_TAG
                '''
            }
        }
        stage('Deploy to EC2 Resume-UI instance') {
            steps {
                sshagent (credentials: [TALKTODB_EC2_SSH_CREDENTIALS]) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no $EC2_USER_AT_TALKTODB_INSTANCE "\
                            aws ecr get-login-password --region $AWS_DEFAULT_REGION | \
                            docker login --username AWS --password-stdin $AWS_ECR_REGISTRY && \
                            docker pull $AWS_ECR_REGISTRY/$IMAGE_REPO_NAME:$IMAGE_TAG && \
                            docker stop $APPLICATION_NAME || true && \
                            docker rm $APPLICATION_NAME || true && \
                            docker run -d -p 80:80 --name $APPLICATION_NAME \
                            $AWS_ECR_REGISTRY/$IMAGE_REPO_NAME:$IMAGE_TAG \
                        "
                    '''
                }
            }
        }
        stage('Clean Up') {
            steps {
                // Optional: Remove the image locally to save space
                sh "docker rmi ${APPLICATION_NAME}:${IMAGE_TAG} || true"
            }
       }
    }
    post {
        success {
            cleanWs()
        }
        failure {
            cleanWs()
        }
    }
}
