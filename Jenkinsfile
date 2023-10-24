pipeline {
  agent any
  options {
    quietPeriod(30)
    buildDiscarder(logRotator(numToKeepStr: '3'))
  }
  environment {
    DEFAULT_BRANCH = 'release-0-X'
  }
  triggers {
    pollSCM('')
  }
  tools {
    maven '3.8.2'
    jdk 'JDK11'
  }
  stages {
    stage('Prepare') {
      steps {
        deleteDir()
        checkout scm
      }
    }
    stage('Build') {
      steps {
        octriMavenBuild(deployArtifacts: env.BRANCH_NAME == env.DEFAULT_BRANCH)
      }
    }
    stage('Test') {
      steps {
        sh 'mvn test'
      }
    }
  }
  post {
    always {
      junit '**/surefire-reports/**/*.xml'
    }
    unsuccessful {
      emailStatusChange()
    }
    fixed {
      emailStatusChange()
    }
  }
}
