job('jslave-container-debug') {
    label('container-fedora-36-stage')
    wrappers {
        timestamps()
    }
    steps {
        shell('printenv && sleep 3600')
    }
}
