job('jslave-openstack-vm-debug') {
    label('vm-rhel-8-5-0-stage')
    wrappers {
        timestamps()
    }
    steps {
        shell('printenv && sleep 3600')
    }
}
