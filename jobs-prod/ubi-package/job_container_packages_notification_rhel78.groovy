// Folder name and description
String folderName = 'ubi-package'
String folderDescription = 'CI for ubi image, docker, podman, buildah, skopeo notification'
// Repository URL
String repo = 'https://gitlab.cee.redhat.com/cloudci/ubi-package.git'
// Packages list
List packages = ['podman', 'docker', 'skopeo', 'buildah', 'runc', 'crun']

folder(folderName) {
    description(folderDescription)
}

packages.each {
    String expected = '.*/' + it + '.*'
    pipelineJob("$folderName/$it") {
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
                                            topic("Consumer.edge-qe-jenkins.${UUID.randomUUID().toString()}.VirtualTopic.eng.brew.task.closed")
                                        }
                                        selector("method = 'build' AND new = 'CLOSED'")
                                        checks {
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
                        branch('*/master')
                    }
                }
                lightweight(true)
                scriptPath('Jenkinsfile.brew')
            }
        }
    }
}
