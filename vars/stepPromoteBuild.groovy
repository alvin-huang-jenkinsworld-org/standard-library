def call(source_repo, target_repo, build_name, build_number) {
  stage("Promote to Production repo") {
    milestone label: 'promotion'  // milestone step here to reject old builds waiting for input
    input 'Promote this build to Production?'
 
    node {
      Artifactory.server(getArtifactoryServerID()).promote([
        'buildName'   : build_name,
        'buildNumber' : build_number,
        'targetRepo'  : target_repo,
        'sourceRepo'  : source_repo,
        'copy'        : true ])
    }
  }
}

def call(source_repo, target_repo) {
  buildInfo = getBuildInfo()

  call(source_repo, target_repo, buildInfo.name, buildInfo.number)
}
