pipeline {
  agent any
  options {
    buildDiscarder(logRotator(numToKeepStr: '3'))
    disableConcurrentBuilds()
  }
  triggers {
    pollSCM('')
  }
  tools {
    maven '3.5.0'
    jdk 'JDK8'
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
        octriArtifactoryBuild(env.BRANCH_NAME)
      }
    }
    stage('Test') {
      steps {
        sh 'mvn test'
      }
    }
  }
  post {
    changed {
      emailStatusChange()
    }
  }
}
