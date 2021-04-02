// Folder name and description
String folderName = 'rhel-edge'
String folderDescription = 'CI for rhel 8 edge image building and installation'
// Job name and description
String jobName = 'rhel_edge_x86_64'
String jobDescription = 'nightly x86_64 compose triggers rhel 8.4.0 edge test'
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
                                        topic("Consumer.rh-jenkins-ci-plugin.${UUID.randomUUID().toString()}.VirtualTopic.eng.pungi.status-change")
                                    }
                                    selector("status = 'FINISHED' AND compose_type = 'nightly' AND release_short = 'RHEL' AND (release_version LIKE '8.3%' OR release_version LIKE '8.4%' OR release_version LIKE '8.5%')")
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
            scriptPath('kitebot/Jenkinsfile')
        }
    }
}
