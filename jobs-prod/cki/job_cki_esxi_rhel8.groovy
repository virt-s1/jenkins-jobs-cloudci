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
    [name: 'rhel-8.0.z', filter: '^rhel80-z$', test_os: 'rhel-8-0'],
].each { Map config ->
    ['bios', 'efi'].each {
        String firmware = it
        pipelineJob("$folderName/${config.name}-esxi-${firmware}") {
            description("run ${config.name} stream kernel test on esxi")
            logRotator(-1, 20, -1, -1)
            parameters {
                stringParam('CI_MESSAGE', '{}', 'Red Hat UMB Message Body')
                stringParam('VSPHERE_SERVER', '10.73.73.245', 'vsphere server IP address')
                stringParam('ESXI_HOST', '10.16.209.80', 'running test on this host')
                stringParam('ESXI_DATACENTER', 'Datacenter7.0', 'datacenter name of above host')
                stringParam('ESXI_DATASTORE', 'datastore-80', 'datacenter name of above host')
                stringParam('ESXI_FIRMWARE', "${firmware}", 'ESXi VM firmware, bios or efi')
                stringParam('TEST_OS', "${config.test_os}", 'test os')
                stringParam('ARCH', 'x86_64', 'CPU architecture')
            }
            properties {
                pipelineTriggers {
                    triggers {
                        ciBuildTrigger {
                            noSquash(true)
                            providers {
                                providerDataEnvelope{
                                    providerData {
                                        activeMQSubscriber {
                                            name("Red Hat UMB")
                                            overrides {
                                                topic("Consumer.rh-jenkins-ci-plugin.${UUID.randomUUID().toString()}.VirtualTopic.eng.cki.ready_for_test")
                                            }
                                            selector("")
                                            checks {
                                                msgCheck {
                                                    field('$.cki_finished')
                                                    expectedValue('false')
                                                }
                                                msgCheck {
                                                    field('$.system[0].stream')
                                                    expectedValue("${config.filter}")
                                                }
                                                msgCheck {
                                                    field('$.build_info[*].architecture')
                                                    expectedValue('x86_64')
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
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
                    scriptPath('cloudci/Jenkinsfile.esxi')
                }
            }
        }
    }
}
