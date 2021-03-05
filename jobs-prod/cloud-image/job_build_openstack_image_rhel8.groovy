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
    // at every quarter i.e on day-of-month 1 in every 3rd month
    [name: 'RHEL-8.3.z', test_os: 'rhel-8-3', cron: '0 0 4 */3 *'],
    [name: 'RHEL-8.4.0', test_os: 'rhel-8-4', cron: '20 5 * * 4'],
].each { Map config ->
    pipelineJob("$folderName/${config.name}-openstack-image") {
        description("build and upload ${config.name} openstack image")
        logRotator(-1, 20, -1, -1)
        parameters {
            stringParam('TEST_OS', "${config.test_os}", 'test os')
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
                scriptPath('cloudci/Jenkinsfile.image.openstack')
            }
        }
    }

}
