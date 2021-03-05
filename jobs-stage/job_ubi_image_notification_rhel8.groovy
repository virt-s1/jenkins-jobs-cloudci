// Folder name and description
String folderName = 'ubi-image'
String folderDescription = 'CI for ubi image notification'
// Job name and description
String jobName = 'image-notification'
String jobDescription = 'ubi8 image for rhel8 notification'
// Repository URL
String repo = 'https://gitlab.cee.redhat.com/cloudci/ubi-package.git'

folder(folderName) {
    description(folderDescription)
}

pipelineJob("$folderName/$jobName") {
    description(jobDescription)
    logRotator(-1, 50, -1, -1)
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
                                    selector("method = 'buildContainer' AND new = 'CLOSED'")
                                    checks {
                                        msgCheck {
                                            field('$.info.request[0]')
                                            expectedValue('.*ubi8.*')
                                        }
                                        msgCheck {
                                            field('$.info.request[1]')
                                            expectedValue('^guest-rhel-8.*-containers')
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
            scriptPath('Jenkinsfile.ubi.stage')
        }
    }
}

