// Folder name and description
String folderName = 'cki-image'
String folderDescription = 'Job to build and upload AWS, Azure, ESXi, and OpenStack image'
// Repository URL
String repo = 'https://github.com/virt-s1/kite-image.git'

folder(folderName) {
    description(folderDescription)
}

[
    // at 00:00 on day-of-month 1 in every 6th month
    [name: 'RHEL-8.2.z', test_os: 'rhel-8-2', cron: '0 0 3 */6 *'],
    [name: 'RHEL-8.4.z', test_os: 'rhel-8-4', cron: '0 0 5 */6 *'],
    [name: 'RHEL-8.6.z', test_os: 'rhel-8-6', cron: '0 0 4 */6 *'],
    [name: 'RHEL-9.0.z', test_os: 'rhel-9-0', cron: '0 0 6 */6 *'],
    [name: 'RHEL-8.7.0', test_os: 'rhel-8-7', cron: '20 7 * * 4'],
    [name: 'RHEL-9.1.0', test_os: 'rhel-9-1', cron: '20 7 * * 4'],
].each { Map config ->
    pipelineJob("$folderName/${config.name}-azure-x86_64-image") {
        description("build and upload ${config.name} azure x86_64 image")
        logRotator(-1, 20, -1, -1)
        parameters {
            stringParam('TEST_OS', "${config.test_os}", 'test os')
            stringParam('ARCH', "x86_64", 'CPU architecture')
        }
        properties {
            pipelineTriggers {
                triggers {
                    cron("${config.cron}")
                }
            }
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
                scriptPath('cloudci/Jenkinsfile.image.azure')
            }
        }
    }
}
