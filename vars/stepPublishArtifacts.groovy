// entrypoint will check whether artifactory s
def call(config) {
  if (config.is_pr) {
    echo "[stdlib] Skipping artifact publishing for PR jobs"
    return
  } else if (config.artifact_pattern == null) {
    echo "[stdlib] No artifact_pattern specified, skipping artifact publishing"
    return
  } else {
      call(config.artifact_pattern, config.dev_repo)
  }
}

def call(pattern, target) {
  stage("Publish to Artifactory (${target})") {
    node {
      // clean the workspace before unstashing
      // existing files in the workspace can cause extra artifacts
      // from previous builds to leak into this build (not desirable)
      // or cause the unstash step to fail if a file can't be overwritten
      deleteDir()

      // unstash artifacts from the build step to use
      unstash 'artifacts'
      // capture build environment info
      server = Artifactory.server(getArtifactoryServerID())

      // create upload spec
      uploadSpec = """{
        "files": [
          {
            "pattern": "${pattern}",
            "target": "${target}"
          }
        ]
      }""".toString()

      server.publishBuildInfo(server.upload(uploadSpec, getBuildInfo()))
    }
  }
}
