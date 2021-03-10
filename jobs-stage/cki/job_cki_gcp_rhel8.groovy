// Folder name and description
String folderName = 'cki'
String folderDescription = 'cki for cloud'
// Repository URL
String repo = 'https://github.com/virt-s1/cki.git'

folder(folderName) {
    description(folderDescription)
}

pipelineJob("$folderName/rhel-8.5-gcp-n2d-standard-2") {
    description("run rhel-8.5 stream kernel test on gcp")
    logRotator(-1, 20, -1, -1)
    parameters {
        stringParam('CI_MESSAGE', '{}', 'Red Hat UMB Message Body')
        stringParam('ARCH', 'x86_64', 'CPU architecture')
        stringParam('TEST_OS', 'rhel-8-5', 'test os')
        stringParam('GCP_INSTANCE_TYPE', 'n2d-standard-2', 'gcp instance type')
    }
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url(repo)
                    }
                    branch('*/master')
                }
            }
            lightweight(true)
            scriptPath("cloudci/Jenkinsfile.gcp.stage")
        }
    }
}
