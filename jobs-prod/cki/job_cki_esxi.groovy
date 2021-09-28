// Folder name and description
String folderName = 'cki'
String folderDescription = 'cki for cloud'
// Repository URL
String repo = 'https://github.com/virt-s1/cki.git'

folder(folderName) {
    description(folderDescription)
}

[
    [name: 'rhel-9.y', filter: '^rhel9$', test_os: 'rhel-9-0', mr_url_check: '.*kernel/centos-.*'],
    [name: 'rhel-8.y', filter: '^rhel8$', test_os: 'rhel-8-5', mr_url_check: '.*kernel/rhel-.*'],
    [name: 'rhel-8.4.z', filter: '^rhel84-z$', test_os: 'rhel-8-4', mr_url_check: '.*kernel/rhel-.*'],
    [name: 'rhel-8.3.z', filter: '^rhel83-z$', test_os: 'rhel-8-3', mr_url_check: '.*kernel/rhel-.*'],
    [name: 'rhel-8.2.z', filter: '^rhel82-z$', test_os: 'rhel-8-2', mr_url_check: '.*kernel/rhel-.*'],
    [name: 'rhel-8.1.z', filter: '^rhel81-z$', test_os: 'rhel-8-1', mr_url_check: '.*kernel/rhel-.*'],
].each { Map config ->
    pipelineJob("$folderName/${config.name}-esxi") {
        description("run ${config.name} stream kernel test on esxi")
        logRotator(-1, 20, -1, -1)
        parameters {
            stringParam('CI_MESSAGE', '{}', 'Red Hat UMB Message Body')
            stringParam('TEST_OS', "${config.test_os}", 'test os')
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
                                            msgCheck {
                                                field('$.merge_request.merge_request_url')
                                                expectedValue("${config.mr_url_check}")
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
