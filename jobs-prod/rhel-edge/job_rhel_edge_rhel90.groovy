// Folder name and description
String folderName = 'rhel-edge'
String folderDescription = 'CI for rhel edge image building and installation'
// Job name and description
String jobName = 'rhel_edge_x86_64_rhel9.0'
String jobDescription = 'nightly rhel compose triggers rhel for edge test'
// Repository URL
String repo = 'https://github.com/virt-s1/rhel-edge.git'

folder(folderName) {
    description(folderDescription)
}

pipelineJob("$folderName/$jobName") {
    description(jobDescription)
    logRotator(-1, 20, -1, -1)
    parameters {
        stringParam('CI_MESSAGE', '{}', 'Red Hat UMB Message Body')
        choiceParam('RHEL90_VIRT', ['true', 'false'], 'Manual trigger RHEL 9.0 virt test')
        choiceParam('RHEL90_NG_VIRT', ['true', 'false'], 'Manual trigger RHEL 9.0 ng virt test')
        choiceParam('RHEL90_RAW', ['true', 'false'], 'Manual trigger RHEL 9.0 raw image test')
        choiceParam('RHEL90_SIMPLIFIED', ['true', 'false'], 'Manual trigger RHEL 9.0 simplified installer test')
        choiceParam('RHEL90_BARE', ['true', 'false'], 'Manual trigger RHEL 9.0 bare metal test')
        choiceParam('RHEL90_REBASE', ['true', 'false'], 'Manual trigger RHEL 9.0 rebase test')
        choiceParam('RHEL90_8TO9', ['true', 'false'], 'Manual trigger RHEL 8 to 9 upgrade test')
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
                                        topic("Consumer.edge-qe-jenkins.${UUID.randomUUID().toString()}.VirtualTopic.eng.cts.compose-tagged")
                                    }
                                    selector("")
                                    checks {
                                        msgCheck {
                                            field('$.tag')
                                            expectedValue('^nightly$')
                                        }
                                        msgCheck {
                                            field('$.compose.compose_info.payload.release.short')
                                            expectedValue('RHEL')
                                        }
                                        msgCheck {
                                            field('$.compose.compose_info.payload.release.version')
                                            expectedValue('9.0.0')
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
            scriptPath('kitebot/Jenkinsfile.90')
        }
    }
}
