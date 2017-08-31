class npmBuild extends baseBuild {

  def getDefaultConfig() {
    return [
      /*
       * NPM builds publish using the native 'npm publish' command so they
       * do not use the artifact_pattern to publish via the standard library.
       * NPM builds are just intermediary and rpms are built from them so they
       * do not need to be promoted to production.
       */
      artifact_pattern: null,
      skip_promote: true,
    ]
  }

  def process(config) {

    node(config.machine) {
      stepBuildArtifacts(config)
      // if we have html publishing params, we will publish them
      if(config.html_pattern){
       stepPublishHTMLReport(config.html_pattern)
      }
    }
  }
}
