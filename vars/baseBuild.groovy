def getDefaultConfig() {
  return [
    artifact_pattern : '*.rpm',
    dev_branch : 'develop',
    release_branch : 'master',
    env : [],
  ]
}

// The call(body) method in any file in workflowLibs.git/vars is exposed as a
// method with the same name as the file.
def call(body) {

  config = getDefaultConfig()

  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()

  /* Set skip_promote if not given in job declaration */
  if (config.get('skip_promote') == null) {
    config.skip_promote = (env.BRANCH_NAME != null) && (env.BRANCH_NAME != config.release_branch)
  }

  /* Set PR flag if not give in job declaration */
  if (config.get('is_pr') == null) {
    config.is_pr = env.BRANCH_NAME != null && env.BRANCH_NAME.startsWith('PR-')
  }

  run(config)
}

def run(config) {
  process(config)

  if (config.skip_promote) {
    echo "[stdlib] Skipping postBuild artifact manipulation for non release branch job or skip_promote is true."
    return
  }

  postBuild(config)
}

def postBuild(config) {
  /*
   * Post-processing tasks - typically promotion
   */

  // Because promotion uses an input closure it
  // should not be called from within the context of
  // a node block

  /* if there is no prod_repo specified, we cannot promote to one */
  if (!config.prod_repo) {
    echo "[stdlib] Skipping promotion of build: No prod_repo specified in Jenkinsfile"
    return
  }
  stepPromoteBuild(config.dev_repo, config.prod_repo)

  /* if jira_ticket is specified, we can create a deploy ticket */
  if(config.jira_ticket) {
    stage ('Creating JIRA Deploy Ticket(s)')
    stepCreateDeployJIRATicket(config.jira_ticket)
  }
}
