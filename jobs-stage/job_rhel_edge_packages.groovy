// Folder name and description
String folderName = 'rhel-edge-packages'
String folderDescription = 'Downstream rhel-edge package CI'
// Repository URL
String repo = 'https://github.com/virt-s1/rhel-edge.git'
// Packages list
List packages = ['greenboot', 'fido-device-onboard', 'rust-coreos-installer', 'rpm-ostree', 'ostree']
// Distro list
List distros = ['el8', 'el9']

folder(folderName) {
    description(folderDescription)
}

packages.each { pkg->
    distros.each { dist->
        String expected = '.*/' + pkg + '.*' + '\\.' + dist + '\\.' + '.*'
        pipelineJob("$folderName/$pkg-$dist") {
            logRotator(-1, 50, -1, -1)
            parameters {
                stringParam('CI_MESSAGE', '{}', 'Red Hat UMB Message Body')
                stringParam('TEST_OS', "${dist}", 'RHEL release version')
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
                                                topic("Consumer.edge-qe-jenkins.${UUID.randomUUID().toString()}.VirtualTopic.eng.brew.task.closed")
                                            }
                                            selector("method = 'buildArch' AND new = 'CLOSED'")
                                            checks {
                                                msgCheck {
                                                    field('$.info.arch')
                                                    expectedValue('x86_64')
                                                }
                                                msgCheck {
                                                    field('$.info.request[0]')
                                                    expectedValue(expected)
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
                            branch('*/main')
                        }
                    }
                    lightweight(true)
                    scriptPath("packages/Jenkinsfile.$pkg")
                }
            }
        }
    }
}
