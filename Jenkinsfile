def deployEnvType = ["uat"]

pipeline {
    agent any
    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'The name of the branch to build and deploy')
        choice(name: 'ENV_TYPE', choices: deployEnvType, description: 'Choose the environment to build and deploy into')
    }
    environment {
        APPLICATION         = "ai-talktodb"
        TEAM                = "westapps"
        ENV_NAME            = "ai"
        ECR_REPO_PREFIX     = "${TEAM}"
        DOCKER_FILE_NAME    = "Dockerfile"
        CODE_BASE_PATH      = "."
        SBT_BUILD_VER        = "sbt 1.7.2"
        SBT_TEST_FLAGS       = " "
        SBT_TEST_CMD         = "clean coverage test coverageReport scapegoat sonarScan"
        SBT_DOCKER_IMAGE     = "sbtscala/scala-sbt:eclipse-temurin-jammy-21.0.2_13_1.9.8_2.13.12"
        SBT_BUILD_FLAGS      = "-Dsbt.log.noformat=true"
        SBT_BUILD_CMD        = "clean universal:packageZipTarball"
        AWS_REGION           = "ap-southeast-2"
        ECR_URI              = "165769518303.dkr.ecr.ap-southeast-2.amazonaws.com/${APPLICATION}"

        SOURCE_DIRS          = "${CODE_BASE_PATH}/src/main" // Comma-separated list of directories that contain source code to analyze
        EXCLUSION_DIRS       = "**/*Bean.scala,**/*DTO.scala"
        LIBRARY_DIRS         = "${HOME}/.ivy2"
        JAR_DIRS             = "${CODE_BASE_PATH}"
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '40'))
        timestamps()
        ansiColor('xterm')
        disableConcurrentBuilds()
    }
    stages {
        stage('Checkout Source') {
            steps {
                git(
                    url: 'https://github.com/westapps/TalkToDB.git',
                    branch: "${BRANCH_NAME}",
                    credentialsId: 'GITHUB_CREDENTIALS'
                )
            }
        }
        stage('Print env') {
            steps {
                script {
                    currentBuild.displayName = "#${BUILD_NUMBER}:${BRANCH_NAME}"
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
        stage('Build container') {
            steps {
                script {
                    unstash(name: "artifact")

                    pipelineBuildContainer(
                        dockerBuildFolder: CODE_BASE_PATH,
                        dockerRepoPrefix: TEAM,
                        dockerfileName: DOCKER_FILE_NAME,
                        taskName: APPLICATION,
                        buildNumber: BUILD_NUMBER,
                        branchName: BRANCH_NAME,
                        generateDockerfile: false,
                        enableJavaOptsCheck: false
                    )
                }
            }
        }
        stage('Push image to AWS ECR') {
            steps {
                script {
                    sh """
                        aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_URI}
                        docker tag ${APPLICATION}:latest ${ECR_URI}:latest
                        docker push ${ECR_URI}:latest
                    """
                }
            }
        }
        stage('Deploy container to EC2') {
            steps {
                script {
                    sshagent(['your-ssh-key-id']) {
                        sh """
                            ssh ec2-user@<your-ec2-instance-ip> << EOF
                                docker pull ${ECR_URI}:latest
                                docker stop ${APPLICATION} || true
                                docker rm ${APPLICATION} || true
                                docker run -d -p 8080:8080 --name ${APPLICATION} -e ENV_TYPE=${ENV_TYPE} ${ECR_URI}:latest
                            EOF
                        """
                    }
                }
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
