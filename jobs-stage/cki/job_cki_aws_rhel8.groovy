// Folder name and description
String folderName = 'cki'
String folderDescription = 'cki for cloud'
// Repository URL
String repo = 'https://github.com/virt-s1/cki.git'

folder(folderName) {
    description(folderDescription)
}

[
    [arch: 'x86_64', instance_type: 'm5dn.large'],
    [arch: 'aarch64', instance_type: 'm6gd.large'],
].each { Map config ->
    pipelineJob("$folderName/rhel-8.5-ec2-${config.instance_type}") {
        description("run rhel-8.5 stream kernel test on aws ec2")
        logRotator(-1, 20, -1, -1)
        parameters {
            stringParam('CI_MESSAGE', '{}', 'Red Hat UMB Message Body')
            stringParam('AWS_REGION', 'us-east-1', 'aws region')
            stringParam('ARCH', "${config.arch}", 'CPU architecture')
            stringParam('TEST_OS', 'rhel-8-5', 'test os')
            stringParam('AWS_INSTANCE_TYPE', "${config.instance_type}", 'aws ec2 instance type')
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
                scriptPath("cloudci/Jenkinsfile.aws.${config.arch}.stage")
            }
        }
    }
}
