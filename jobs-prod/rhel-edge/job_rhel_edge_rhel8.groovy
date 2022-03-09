// Folder name and description
String folderName = 'rhel-edge'
String folderDescription = 'CI for rhel 8 edge image building and installation'
// Job name and description
String jobName = 'rhel_edge_x86_64_rhel8'
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
        choiceParam('RHEL85_VIRT', ['true', 'false'], 'Manual trigger RHEL 8.5 virt test')
        choiceParam('RHEL85_NG_VIRT', ['true', 'false'], 'Manual trigger RHEL 8.5 ng virt test')
        choiceParam('RHEL85_RAW', ['true', 'false'], 'Manual trigger RHEL 8.5 raw image test')
        choiceParam('RHEL85_SIMPLIFIED', ['true', 'false'], 'Manual trigger RHEL 8.5 simplified installer test')
        choiceParam('RHEL85_BARE', ['true', 'false'], 'Manual trigger RHEL 8.5 bare metal test')
        choiceParam('RHEL86_VIRT', ['true', 'false'], 'Manual trigger RHEL 8.6 virt test')
        choiceParam('RHEL86_NG_VIRT', ['true', 'false'], 'Manual trigger RHEL 8.6 ng virt test')
        choiceParam('RHEL86_RAW', ['true', 'false'], 'Manual trigger RHEL 8.6 raw image test')
        choiceParam('RHEL86_SIMPLIFIED', ['true', 'false'], 'Manual trigger RHEL 8.6 simplified installer test')
        choiceParam('RHEL86_REBASE', ['true', 'false'], 'Manual trigger RHEL 8.6 rebase test')
        choiceParam('RHEL86_BARE', ['true', 'false'], 'Manual trigger RHEL 8.6 bare metal test')
        choiceParam('CS8_VIRT', ['true', 'false'], 'Manual trigger CS8 virt test')
        choiceParam('CS8_NG_VIRT', ['true', 'false'], 'Manual trigger CS8 ng virt test')
        choiceParam('CS8_RAW', ['true', 'false'], 'Manual trigger CS8 raw image test')
        choiceParam('CS8_SIMPLIFIED', ['true', 'false'], 'Manual trigger CS8 simplified installer test')
        choiceParam('CS8_REBASE', ['true', 'false'], 'Manual trigger CS8 rebase test')
        choiceParam('CS8_BARE', ['true', 'false'], 'Manual trigger CS8 bare metal test')
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
                                        topic("Consumer.edge-qe-jenkins.${UUID.randomUUID().toString()}.VirtualTopic.eng.pungi.status-change")
                                    }
                                    selector("status = 'FINISHED' AND compose_type = 'nightly' AND release_short = 'RHEL' AND (release_version LIKE '8.5%')")
                                }
                            }
                        }
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
                                            expectedValue('8.6.0')
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
            scriptPath('kitebot/Jenkinsfile.8')
        }
    }
}
