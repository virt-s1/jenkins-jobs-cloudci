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
    projectFactories {
        workflowMultiBranchProjectFactory {
            scriptPath('kitebot/Jenkinsfile')
        }
    }
    orphanedItemStrategy {
        discardOldItems {
            daysToKeep(2)
            numToKeep(5)
        }
    }
}
