// Folder name and description
String folderName = 'cki'
String folderDescription = 'cki for cloud'
// Repository URL
String repo = 'https://github.com/virt-s1/cki.git'

folder(folderName) {
    description(folderDescription)
}

['bios', 'efi'].each {
    String firmware = it
    pipelineJob("$folderName/rhel-8.5-esxi-${firmware}") {
        description("run rhel-8.5 stream kernel test on esxi")
        logRotator(-1, 20, -1, -1)
        parameters {
            stringParam('CI_MESSAGE', '{}', 'Red Hat UMB Message Body')
            stringParam('VSPHERE_SERVER', '10.73.73.245', 'vsphere server IP address')
            stringParam('ESXI_HOST', '10.16.209.80', 'running test on this host')
            stringParam('ESXI_DATACENTER', 'Datacenter7.0', 'datacenter name of above host')
            stringParam('ESXI_DATASTORE', 'datastore-80', 'datacenter name of above host')
            stringParam('ESXI_FIRMWARE', "${firmware}", 'ESXi VM firmware, bios or efi')
            stringParam('TEST_OS', 'rhel-8-5', 'test os')
            stringParam('ARCH', 'x86_64', 'CPU architecture')
        }
        definition {
            cpsScm {
                scm {
                    git {
                        remote {
                            url(repo)
                        }
                        branch('*/stage')
                    }
                }
                lightweight(true)
                scriptPath('cloudci/Jenkinsfile.esxi')
            }
        }
    }
}
