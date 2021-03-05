// Folder name and description
String folderName = 'cockpit-composer'
String folderDescription = 'CI for cockpit-composer'
// Job name and description
String jobName = 'tier-1'
String jobDescription = 'cockpit-composer tier-1 CI for RHEL-8'
// Repository URL
String repo = 'https://gitlab.cee.redhat.com/cloudci/cockpit-composer.git'

folder(folderName) {
    description(folderDescription)
}

pipelineJob("$folderName/$jobName") {
    description(jobDescription)
    logRotator(-1, 20, -1, -1)
    parameters {
        stringParam('CI_MESSAGE', '{}', 'Red Hat UMB Message Body')
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
                                        topic("Consumer.rh-jenkins-ci-plugin.${UUID.randomUUID().toString()}.VirtualTopic.eng.brew.task.closed")
                                    }
                                    selector("method = 'build' AND new = 'CLOSED'")
                                    checks {
                                        msgCheck {
                                            field('$.info.request[0]')
                                            expectedValue('.*cockpit-composer.*')
                                        }
                                        msgCheck {
                                            field('$.info.request[1]')
                                            expectedValue('^rhel-8.*')
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
            scriptPath('Jenkinsfile')
        }
    }
}
