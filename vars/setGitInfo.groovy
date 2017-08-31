def call() {
  /*
   * Unfortunately the git info is inaccessible from Pipeline's scope. Perform
   * this nastiness to get what git info we need into the environment.
   *
   * https://issues.jenkins-ci.org/browse/JENKINS-35230
   * http://stackoverflow.com/questions/35554983/git-variables-in-jenkins-workflow-plugin
   * http://stackoverflow.com/questions/35873902/accessing-scm-git-variables-on-a-jenkins-pipeline-job/35911754
   */

  // get commit hash
  sh 'git rev-parse HEAD > GIT_COMMIT'
  env.GIT_COMMIT = readFile('GIT_COMMIT').trim()

  // get git url
  sh 'git config remote.origin.url > GIT_URL'
  env.GIT_URL = readFile('GIT_URL').trim()

  // get commit hash
  sh 'git log -1 --pretty="%an" > GIT_AUTHOR'
  env.GIT_AUTHOR = readFile('GIT_AUTHOR').trim()

  // get repo name
  sh 'basename $(git config remote.origin.url) .git > GIT_REPO_NAME || true'
  env.GIT_REPO_NAME = readFile('GIT_REPO_NAME').trim()
}
