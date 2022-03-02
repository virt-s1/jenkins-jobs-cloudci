// Folder name and description
String folderName = 'rhel-edge'
String folderDescription = 'CI for rhel 8 edge image building and installation'
// Job name and description
String jobName = 'build_rhel_edge_image'
String jobDescription = 'daily build container image and running on OCP4'
// Repository URL
String repo = 'https://github.com/virt-s1/rhel-edge.git'

folder(folderName) {
    description(folderDescription)
}

pipelineJob("$folderName/$jobName") {
    description(jobDescription)
    logRotator(-1, 20, -1, -1)
    parameters {
        choiceParam('RHEL86', ['true', 'false'], 'Manual trigger RHEL 8.6 image building')
        choiceParam('RHEL86-RT', ['true', 'false'], 'Manual trigger RHEL 8.6 rt kernel image building')
        choiceParam('RHEL90', ['true', 'false'], 'Manual trigger RHEL 9.0 image building')
        choiceParam('RHEL90-RT', ['true', 'false'], 'Manual trigger RHEL 9.0 rt kernel image building')
    }
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url(repo)
                    }
                    branch('*/main')
                }
            }
            lightweight(true)
            scriptPath('kitebot/Jenkinsfile.build')
        }
    }
}
