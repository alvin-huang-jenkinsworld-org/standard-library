// Step to run build scripts based on pr_script, dev_script and release_script
def call(config) {
  stage("Build") {
    /* config.env SHOULD BE passed in as an ArrayList and therefore can be used directly */
    withEnv(config.get('env', [])) {
      /* Different behavior is required here based on whether or not this is a
         Pull Request. */

      /* If this is a PR and a config.pr_script is set, run the
         pr_script and skip running the dev_script or release_script by returning
         early */
      if (config.is_pr) {
        if (config.pr_script == null) {
          echo "[stdlib] No pr_script specified for PR"
          return
        } else {
          echo "[stdlib] Executing pr_script for PR"
          sh config.pr_script
        }
      }

      /* If dev_branch and config.dev_script is defined, run it */
      else if (env.BRANCH_NAME ==~ config.dev_branch && env.BRANCH_NAME != config.release_branch) {
        if (config.dev_script == null) {
          echo "[stdlib] No dev_script specified for dev branch: ${config.dev_branch}"
          return
        } else {
          echo "[stdlib] Executing dev_script for dev branch: ${config.dev_branch}"
          sh config.dev_script
        }
      }

      /* If release_branch and config.release_script is defined, run it */
      else if (env.BRANCH_NAME ==~ config.release_branch) {
        if (config.release_script == null) {
          echo "[stdlib] No release_script specified for release branch: ${config.release_branch}"
          return
        } else {
          echo "[stdlib] Executing release_script for release branch: ${config.release_branch}"
          sh config.release_script
        }
      }
      else {
        echo "[stdlib] No script was run for this feature branch: ${env.BRANCH_NAME}"
      }
    }
  }
}

