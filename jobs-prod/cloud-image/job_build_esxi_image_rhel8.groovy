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
    pipelineJob("$folderName/${config.name}-esxi-image") {
        description("build and upload ${config.name} esxi image")
        logRotator(-1, 20, -1, -1)
        parameters {
            stringParam('VSPHERE_SERVER', '10.73.73.245', 'vsphere server IP address')
            stringParam('ESXI_HOST', '10.16.209.80', 'running test on this host')
            stringParam('ESXI_DATACENTER', 'Datacenter7.0', 'datacenter name of above host')
            stringParam('ESXI_DATASTORE', 'datastore-80', 'datacenter name of above host')
            stringParam('TEST_OS', "${config.test_os}", 'test os')
            stringParam('ARCH', 'x86_64', 'CPU architecture')
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
                scriptPath('cloudci/Jenkinsfile.image.esxi')
            }
        }
    }

}
