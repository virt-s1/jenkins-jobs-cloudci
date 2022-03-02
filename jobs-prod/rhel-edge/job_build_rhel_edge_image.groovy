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
