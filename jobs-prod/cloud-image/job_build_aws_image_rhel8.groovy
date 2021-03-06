// Folder name and description
String folderName = 'cki-image'
String folderDescription = 'Job to build and upload AWS, ESXi, and OpenStack image'
// Repository URL
String repo = 'https://github.com/virt-s1/kite-image.git'

folder(folderName) {
    description(folderDescription)
}

[
    // at 00:00 on day-of-month 1 in every 6th month
    [name: 'RHEL-8.0.z', test_os: 'rhel-8-0', cron: '0 0 1 */6 *'],
    [name: 'RHEL-8.1.z', test_os: 'rhel-8-1', cron: '0 0 2 */6 *'],
    [name: 'RHEL-8.2.z', test_os: 'rhel-8-2', cron: '0 0 3 */6 *'],
    [name: 'RHEL-8.3.z', test_os: 'rhel-8-3', cron: '0 0 4 */6 *'],
    [name: 'RHEL-8.4.z', test_os: 'rhel-8-4', cron: '0 0 5 */6 *'],
    [name: 'RHEL-8.5.0', test_os: 'rhel-8-5', cron: '20 5 * * 4'],
].each { Map config ->
    ['x86_64', 'aarch64'].each {
        String arch = it
        pipelineJob("$folderName/${config.name}-aws-${arch}-image") {
            description("build and upload ${config.name} aws ${arch} image")
            logRotator(-1, 20, -1, -1)
            parameters {
                stringParam('AWS_REGION', 'us-east-1', 'aws region')
                stringParam('TEST_OS', "${config.test_os}", 'test os')
                stringParam('ARCH', "${arch}", 'CPU architecture')
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
                    scriptPath('cloudci/Jenkinsfile.image.aws')
                }
            }
        }
    }
}
