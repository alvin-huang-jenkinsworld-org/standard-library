// This step checks out code along with any clone parameters passed in
def call(config) {
  stage("Checkout SCM") {

    // This replaces 'checkout scm' so we can checkout to a local branch instead of detached head.
    // This will be useful in case we need to write something back to github from Jenkins.
    clone_options = config.clone_options ?: [:]

    checkout ([
        $class: 'GitSCM',
        branches: scm.branches,
        userRemoteConfigs: scm.userRemoteConfigs,
        extensions: scm.extensions + [
          [$class: 'LocalBranch',
           localBranch: "${env.BRANCH_NAME}"]] +
          [[$class: 'CloneOption',
           reference: clone_options.reference ?: '',
           depth: clone_options.depth ?: 0,
           shallow: clone_options.shallow ?: false,
           noTags: clone_options.noTags ?: false]]
        ])
    // Load some git info into the environment (commit ID, tags, etc)
    setGitInfo()
  }
}
