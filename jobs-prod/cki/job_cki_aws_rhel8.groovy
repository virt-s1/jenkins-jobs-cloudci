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
    [
        [arch: 'x86_64', instances: ['t2.medium', 't3a.medium', 'm4.large', 'm5.large', 'm5dn.large']],
        [arch: 'aarch64', instances: ['a1.large', 'm6gd.large', 't4g.medium']],
    ].each { Map aws ->
        aws.instances.each {
            String instance_type = it
            pipelineJob("$folderName/${config.name}-ec2-${instance_type}") {
                description("run ${config.name} stream kernel test on aws ec2")
                logRotator(-1, 20, -1, -1)
                parameters {
                    stringParam('CI_MESSAGE', '{}', 'Red Hat UMB Message Body')
                    stringParam('AWS_REGION', 'us-east-1', 'aws region')
                    stringParam('ARCH', "${aws.arch}", 'CPU architecture')
                    stringParam('TEST_OS', "${config.test_os}", 'test os')
                    stringParam('AWS_INSTANCE_TYPE', "${instance_type}", 'aws ec2 instance type')
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
                                                        expectedValue("${aws.arch}")
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
                        scriptPath("cloudci/Jenkinsfile.aws.${aws.arch}")
                    }
                }
            }
        }
    }
}
