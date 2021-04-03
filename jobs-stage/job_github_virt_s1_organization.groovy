organizationFolder('Virt-S1') {
    description('Github Organization Virt-S1')
    displayName('Virt-S1')
    organizations {
        github {
            apiUri('https://api.github.com')
            credentialsId('github-cloudkitebot-credential')
            repoOwner('virt-s1')
            traits {
                gitHubBranchDiscovery {
                    strategyId(1)
                }
                gitHubPullRequestDiscovery {
                    strategyId(2)
                }
                notificationContextTrait {
                    typeSuffix(true)
                    contextLabel('kitebot')
                }
            }
        }
    }
    configure {
        def traits = it / navigators / 'org.jenkinsci.plugins.github__branch__source.GitHubSCMNavigator' / traits
        traits << 'org.jenkinsci.plugins.github_branch_source.ForkPullRequestDiscoveryTrait' {
            strategyId 2
            trust(class: 'org.jenkinsci.plugins.github_branch_source.ForkPullRequestDiscoveryTrait$TrustPermission')
        }
    }
    projectFactories {
        workflowMultiBranchProjectFactory {
            scriptPath('kitebot/Jenkinsfile')
        }
    }
    triggers {
        periodicFolderTrigger {
            interval('1440')
        }
    }
    orphanedItemStrategy {
        discardOldItems {
            daysToKeep(2)
            numToKeep(5)
        }
    }
}
