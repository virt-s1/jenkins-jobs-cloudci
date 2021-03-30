// Folder name and description
String folderName = 'cki'
String folderDescription = 'cki for cloud'
// Repository URL
String repo = 'https://github.com/virt-s1/cki.git'

folder(folderName) {
    description(folderDescription)
}

pipelineJob("$folderName/rhel-8.5-esxi") {
    description("run rhel-8.5 stream kernel test on esxi")
    logRotator(-1, 20, -1, -1)
    parameters {
        stringParam('CI_MESSAGE', '{}', 'Red Hat UMB Message Body')
        stringParam('TEST_OS', 'rhel-8-5', 'test os')
    }
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url(repo)
                    }
                    branch('*/parallel')
                }
            }
            lightweight(true)
            scriptPath("cloudci/Jenkinsfile.parallel.esxi")
        }
    }
}
