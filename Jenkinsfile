pipeline {
    agent {
        docker {
            label 'main'
            image docker.build("storj-ci", "--pull git://github.com/storj/ci.git#main").id
            args '-u root:root --cap-add SYS_PTRACE -v "/tmp/gomod":/go/pkg/mod'
        }
    }
    options {
          timeout(time: 26, unit: 'MINUTES')
    }
    stages {
        stage('Build') {
            steps {
                checkout scm

                sh './scripts/build.sh'

            }
        }
    }

    post {
        always {
            sh "chmod -R 777 ." // ensure Jenkins agent can delete the working directory
            deleteDir()
        }
    }
}
