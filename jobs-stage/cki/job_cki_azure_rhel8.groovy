// Folder name and description
String folderName = 'cki'
String folderDescription = 'cki for cloud'
// Repository URL
String repo = 'https://github.com/virt-s1/cki.git'

folder(folderName) {
    description(folderDescription)
}

[
    [name: 'rhel-8.y', filter: '^rhel8$', test_os: 'rhel-8-5'],
    [name: 'rhel-8.4.z', filter: '^rhel84-z$', test_os: 'rhel-8-4'],
    [name: 'rhel-8.3.z', filter: '^rhel83-z$', test_os: 'rhel-8-3'],
    [name: 'rhel-8.2.z', filter: '^rhel82-z$', test_os: 'rhel-8-2'],
    [name: 'rhel-8.1.z', filter: '^rhel81-z$', test_os: 'rhel-8-1'],
].each { Map config ->
    pipelineJob("$folderName/${config.name}-azure") {
        description("run ${config.name} stream kernel test on azure")
        logRotator(-1, 20, -1, -1)
        parameters {
            stringParam('CI_MESSAGE', '{}', 'Red Hat UMB Message Body')
            stringParam('TEST_OS', "${config.test_os}", 'test os')
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
                scriptPath("cloudci/Jenkinsfile.azure")
            }
        }
    }
}
