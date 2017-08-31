/*
 * This component operates on the following config members
 *
 *  - is_pr:
 *      A boolean indicating whether or not the current job has been
 *      triggered from a Pull Request. Artifacts are not published or
 *      promoted when `is_pr` is true
 *
 *  - artifact_pattern:
 *      A glob pattern suitable for use with Artifactory to select
 *      artifacts for publishing, i.e. `*.rpm`
 *
 *  - dev_repo:
 *      The development repository in Artifactory to which artifacts
 *      should be published
 */
def call(config) {
  echo "[stdlib] in stepBuildArtifacts"
  deleteDir()
  stepCheckout(config)
  stepRunStandardScripts(config)
  if (config.is_pr) {
    echo "[stdlib] Skipping artifact stashing for PR jobs"
    return
  } else if (config.artifact_pattern == null) {
    echo "[stdlib] No artifact_pattern specified, skipping artifact stashing"
    return
  } else {
    /* Add hack for stash recursion */
    stash_pattern = config.artifact_pattern
    // if artifact pattern is *.(extension), we will recursively search
    if (config.artifact_pattern =~ /^\*{1}/) {
      stash_pattern = '**/' + config.artifact_pattern
      echo "[stdlib] artifact pattern is ${stash_pattern}"
    }
    // stashing artifacts so we can publish them
    stash includes: "${stash_pattern}", name: 'artifacts'
  }
}
