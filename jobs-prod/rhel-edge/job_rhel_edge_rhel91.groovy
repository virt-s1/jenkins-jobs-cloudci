// Folder name and description
String folderName = 'rhel-edge'
String folderDescription = 'CI for rhel edge image building and installation'
// Job name and description
String jobName = 'rhel_edge_x86_64_rhel9.1'
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
        choiceParam('RHEL91_VIRT', ['true', 'false'], 'Manual trigger RHEL 9.1 virt test')
        choiceParam('RHEL91_NG_VIRT', ['true', 'false'], 'Manual trigger RHEL 9.1 ng virt test')
        choiceParam('RHEL91_RAW', ['true', 'false'], 'Manual trigger RHEL 9.1 raw image test')
        choiceParam('RHEL91_SIMPLIFIED', ['true', 'false'], 'Manual trigger RHEL 9.1 simplified installer test')
        choiceParam('RHEL91_BARE', ['true', 'false'], 'Manual trigger RHEL 9.1 bare metal test')
        choiceParam('RHEL91_REBASE', ['true', 'false'], 'Manual trigger RHEL 9.1 rebase test')
        choiceParam('RHEL91_8TO9', ['true', 'false'], 'Manual trigger RHEL 8 to 9 upgrade test')
        choiceParam('CS9_VIRT', ['true', 'false'], 'Manual trigger CS9 virt test')
        choiceParam('CS9_NG_VIRT', ['true', 'false'], 'Manual trigger CS9 ng virt test')
        choiceParam('CS9_RAW', ['true', 'false'], 'Manual trigger CS9 raw image test')
        choiceParam('CS9_SIMPLIFIED', ['true', 'false'], 'Manual trigger CS9 simplified installer test')
        choiceParam('CS9_BARE', ['true', 'false'], 'Manual trigger CS9 bare metal test')
        choiceParam('CS9_REBASE', ['true', 'false'], 'Manual trigger CS9 rebase test')
        choiceParam('CS9_8TO9', ['true', 'false'], 'Manual trigger CS8 to CS9 upgrade test')
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
                                            expectedValue('9.1.0')
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
            scriptPath('kitebot/Jenkinsfile.91')
        }
    }
}
