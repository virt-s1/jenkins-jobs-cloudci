job('SEED') {
    label('container-fedora-36-prod')
    scm {
        git{
            remote {
                url('https://github.com/virt-s1/jenkins-jobs-cloudci.git')
            }
            branch('*/master')
        }
    }
    triggers {
        pollSCM {
            scmpoll_spec('H/15 * * * *')
        }
    }
    wrappers {
        timestamps()
    }
    steps {
        jobDsl {
            removedConfigFilesAction('DELETE')
            removedJobAction('DELETE')
            removedViewAction('DELETE')
            targets('jobs-prod/**/job_*.groovy')
            sandbox(true)
        }
    }
}
queue('SEED')
